package com.swacky.ohmega.event;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.network.C2S.ReloadDataPacket;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

@EventBusSubscriber(modid = Ohmega.MODID)
public final class CommonEvents {
    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();

        if (!event.isWasDeath() || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            Player newPlayer = event.getEntity();

            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
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
    public static void onLivingPostTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            CommonCallbacks.onLivingPostTick(entity);
        }
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
    public static void onRegisterNetwork(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0")
                .playToServer(
                        ReloadDataPacket.TYPE,
                        ReloadDataPacket.CODEC,
                        new MainThreadPayloadHandler<>((_, context) ->
                                OhmegaNetworking.C2S.handleReloadContainer((ServerPlayer) context.player())))
                .playToServer(
                        SetExtensionVisiblePacket.TYPE,
                        SetExtensionVisiblePacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, context) ->
                                OhmegaNetworking.C2S.handleSetExtensionVisible(packet, (ServerPlayer) context.player())))
                .playToServer(
                        SetHiddenPacket.TYPE,
                        SetHiddenPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, context) ->
                                OhmegaNetworking.C2S.handleSetHidden(packet, (ServerPlayer) context.player())))
                .playToServer(
                        UseAccessoryPacket.TYPE,
                        UseAccessoryPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, context) ->
                                OhmegaNetworking.C2S.handleUseAccessory(packet, (ServerPlayer) context.player())))
                .playToClient(
                        SyncHiddenPacket.TYPE,
                        SyncHiddenPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncHidden(packet)))
                .playToClient(
                        SyncStacksPacket.TYPE,
                        SyncStacksPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncStacks(packet)))
                .configurationToClient(
                        SyncTypesPacket.TYPE,
                        SyncTypesPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, context) -> {
                            if (context.connection().getPacketListener() instanceof ClientConfigurationPacketListenerImpl listener) {
                                OhmegaNetworking.S2C.handleSyncTypes(packet, listener.receivedRegistries);
                            }
                        }))
                .playToClient(
                        SyncUsePacket.TYPE,
                        SyncUsePacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncUse(packet)));
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Ohmega.id("accessory_type_manager"), AccessoryTypeManager.getInstance());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartedEvent event) {
        CommonCallbacks.onSetupAccessoryTypeManager();
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null && !accessory.preferVanillaUse(stack)) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, stack);

            if (candidate.consumesAction()) {
                event.setCancellationResult(candidate);
            }
        }
    }
}
