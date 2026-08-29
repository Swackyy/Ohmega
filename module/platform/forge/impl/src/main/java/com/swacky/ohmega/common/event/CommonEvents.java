package com.swacky.ohmega.common.event;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.event.CommonCallbacks;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.common.dataattachment.AccessoryDataProvider;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Ohmega.MODID)
public final class CommonEvents {
    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent.Entities event) {
        event.addCapability(AccessoryDataProvider.CAPABILITY_ID, new AccessoryDataProvider());
    }

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();

        oldPlayer.reviveCaps();
        CommonCallbacks.onClonePlayer((ServerPlayer) oldPlayer, (ServerPlayer) event.getEntity(), !event.isWasDeath());
        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            CommonCallbacks.onServerConfigLoad();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            CommonCallbacks.onServerConfigReload();
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            OhmegaDataAttachments.getData(entity).onAttach(entity);
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
        CommonCallbacks.onSetupAccessoryTypeManager();
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null && !accessory.preferVanillaUse(stack)) {
            InteractionResult candidate = OhmegaDataAttachments.getData(player).tryEquip(player, stack);

            if (candidate.consumesAction()) {
                event.setCancellationResult(candidate);
            }
        }
    }
}
