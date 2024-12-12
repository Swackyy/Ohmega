package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.ModNetworking;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.config.SimpleConfigurationTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = OhmegaCommon.MODID)
public class CommonForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer player && event.getEntity() instanceof ServerPlayer player0) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(player0));
        }
    }

    @SubscribeEvent
    public static void attachCapsPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(OhmegaCommon.rl("accessory_container"), new AccessoryContainerProvider(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AccessoryHelper.getContainer(event.player).ifPresent(AccessoryContainer::tick);
        }
    }

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
            case ON -> false;
            case OFF -> true;
            case DEFAULT -> oldPlayer.getServer() == null || !oldPlayer.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        };

        oldPlayer.reviveCaps();
        if (event.isWasDeath() || flag) {
            AccessoryHelper.getContainer(oldPlayer).ifPresent(oldA -> AccessoryHelper.getContainer(newPlayer).ifPresent(newA -> {
                RegistryAccess access = oldPlayer.registryAccess();
                newA.deserializeNBT(access, oldA.serializeNBT(access));
            }));
        }
        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
                case ON -> false;
                case OFF -> true;
                case DEFAULT -> player.getServer() == null || !player.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            };

            if (flag) {
                AccessoryHelper.getContainer(player).ifPresent(AccessoryContainer::invalidate);
            }
        }
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = AccessoryHelper.tryEquip(event.getEntity(), event.getHand()).getResult();
        if (result == InteractionResult.SUCCESS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void addResourceReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AccessoryTypeManager.getInstance());
    }

    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(OhmegaCommon.rl("sync_accessory_types").toString());
    @SubscribeEvent
    public static void addConfigTask(GatherLoginConfigurationTasksEvent event) {
        event.addTask(new SimpleConfigurationTask(TYPE,
                () -> ModNetworking.send(new SyncAccessoryTypesPacket(AccessoryTypeManager.getInstance().getTypes()), event.getConnection())));
    }

    @SuppressWarnings("deprecation")
    private static class AccessoryContainerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final AccessoryContainer inner;
        private final LazyOptional<AccessoryContainer> cap;
        private final Player player;

        public AccessoryContainerProvider(Player player) {
            this.inner = new AccessoryContainer(player);
            this.cap = LazyOptional.of(() -> this.inner);
            this.player = player;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return Ohmega.ACCESSORIES.orEmpty(cap, this.cap);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
            return this.inner.serializeNBT(this.player.registryAccess());
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag tag) {
            this.inner.deserializeNBT(this.player.registryAccess(), tag);
        }
    }
}
