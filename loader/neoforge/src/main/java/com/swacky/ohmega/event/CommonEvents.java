package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.command.OhmegaRootCommand;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.ReloadDataPacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
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
            OhmegaNetworking.S2C.send(player, new SyncTypesPacket());
            AccessoryHelper.getData(player).onAttach(player);
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
        OhmegaRootCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void onRegisterNetwork(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0")
                .playToServer(
                        OpenAccessoryInventoryPacket.TYPE,
                        OpenAccessoryInventoryPacket.CODEC,
                        new MainThreadPayloadHandler<>((_, context) ->
                                OhmegaNetworking.C2S.handleOpenAccessoryInventory((ServerPlayer) context.player())))
                .playToServer(
                        ReloadDataPacket.TYPE,
                        ReloadDataPacket.CODEC,
                        new MainThreadPayloadHandler<>((_, context) ->
                                OhmegaNetworking.C2S.handleReloadContainer((ServerPlayer) context.player())))
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
                .playToClient(
                        SyncTypesPacket.TYPE,
                        SyncTypesPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncTypes(packet)))
                .playToClient(
                        SyncUsePacket.TYPE,
                        SyncUsePacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncUse(packet)));
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Ohmega.RELOAD_LISTENER_ID, AccessoryTypeManager.getInstance());
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        Item item = stack.getItem();

        if (AccessoryHelper.isAccessory(item) && !AccessoryHelper.getAccessory(item).preferVanillaUse(stack)) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, stack);

            if (candidate.consumesAction()) {
                event.setCancellationResult(candidate);
            }
        }
    }
}
