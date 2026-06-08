package com.swacky.ohmega.event;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.OhmegaMain;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jspecify.annotations.NonNull;

@Mod.EventBusSubscriber(modid = Ohmega.MODID)
public final class CommonEvents {
    private static final Identifier CAPABILITY_ID = Ohmega.id("accessory_data");

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent.Entities event) {
        event.addCapability(CAPABILITY_ID, new AccessoryDataProvider());
    }

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();

        if (!event.isWasDeath() || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            Player newPlayer = event.getEntity();

            oldPlayer.reviveCaps();
            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
            oldPlayer.invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            CommonCallbacks.onServerConfigReload();
        }
    }

    @SubscribeEvent
    public static void onLivingDropItems(LivingDropsEvent event) {
        CommonCallbacks.onLivingDeath(event.getEntity(), event.getDrops());
    }

    @SubscribeEvent
    public static void onModifyCreativeOpBlocksTab(BuildCreativeModeTabContentsEvent event) {
        if (event.hasPermissions() && event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.accept(OhmegaItems.getAngelRing());
        }
    }

    @SubscribeEvent
    public static void onModifyLivingVisibility(LivingEvent.LivingVisibilityEvent event) {
        event.modifyVisibility(CommonCallbacks.getVisibilityPercentModifier(event.getEntity(), event.getLookingEntity()));
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CommonCallbacks.onPlayerChangeDimension(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.getData(player).onAttach(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawnPost(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();

        AccessoryHelper.getData(player).onAttach(player);
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer tracker && event.getTarget() instanceof LivingEntity tracked) {
            CommonCallbacks.onLivingTrack(tracker, tracked);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommonCallbacks.onRegisterCommands(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AccessoryTypeManager.getInstance());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartedEvent event) {
        CommonCallbacks.onServerStarting();
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null && !accessory.preferVanillaUse(stack)) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, stack);

            if (candidate.consumesAction()) {
                event.setCancellationResult(candidate);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static class AccessoryDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private AccessoryData data;
        private final LazyOptional<AccessoryData> capabilityOpt;

        public AccessoryDataProvider() {
            this.data = new AccessoryData();
            this.capabilityOpt = LazyOptional.of(() -> this.data);
        }

        @NonNull
        @Override
        public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, Direction side) {
            return OhmegaMain.ACCESSORIES.orEmpty(cap, capabilityOpt);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
            return (CompoundTag) AccessoryData.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), data)
                    .result().orElseGet(CompoundTag::new);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag tag) {
            AccessoryData.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), tag).resultOrPartial().ifPresent(data -> this.data = data);
        }
    }
}
