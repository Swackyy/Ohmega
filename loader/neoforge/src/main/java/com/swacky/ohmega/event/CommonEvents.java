package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.function.Consumer;

@EventBusSubscriber(modid = OhmegaCommon.MODID)
public final class CommonEvents {
    private static final ResourceLocation RELOAD_LISTENER_ID = OhmegaCommon.rl("accessory_type_manager");
    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(OhmegaCommon.rl("sync_accessory_types"));

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();

        if (!event.isWasDeath() || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            Player newPlayer = event.getEntity();

            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
        }
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = AccessoryHelper.tryEquip(event.getEntity(), event.getHand());

        event.setCancellationResult(result);

        if (result == InteractionResult.SUCCESS) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.getContainer(player).onDeath(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.syncAllSlots(player, Collections.singleton(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        CommonCallbacks.onPlayerPostTick(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer tracked && event.getEntity() instanceof ServerPlayer tracker) {
            CommonCallbacks.onPlayerTrack(tracker, tracked);
        }
    }

    @SubscribeEvent
    public static void onRegisterConfigTasks(RegisterConfigurationTasksEvent event) {
        event.register(new ICustomConfigurationTask() {
            @Override
            public void run(@NonNull Consumer<CustomPacketPayload> consumer) {
                consumer.accept(new SyncAccessoryTypesPacket());
                event.getListener().finishCurrentTask(TYPE);
            }

            @Override
            public @NonNull Type type() {
                return TYPE;
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterNetwork(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0")
                .playToServer(
                        OpenAccessoryInventoryPacket.TYPE,
                        OpenAccessoryInventoryPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.C2S::handleOpenAccessoryInventory))
                .playToServer(
                        OpenInventoryPacket.TYPE,
                        OpenInventoryPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.C2S::handleOpenInventory))
                .playToServer(
                        ResizeContainerPacket.TYPE,
                        ResizeContainerPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.C2S::handleResizeContainer))
                .playToServer(
                        UseAccessoryPacket.TYPE,
                        UseAccessoryPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.C2S::handleUseAccessory))
                .playToClient(
                        SyncAccessorySlotsPacket.TYPE,
                        SyncAccessorySlotsPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.S2C::handleSyncAccessorySlots))
                .configurationToClient(
                        SyncAccessoryTypesPacket.TYPE,
                        SyncAccessoryTypesPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.S2C::handleSyncAccessoryTypes));
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(RELOAD_LISTENER_ID, AccessoryTypeManager.getInstance());
    }
}
