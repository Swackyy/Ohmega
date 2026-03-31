package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

@EventBusSubscriber(modid = Ohmega.MODID)
public final class CommonEvents {
    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(Ohmega.id("sync_accessory_types"));

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
        if (event.getEntity() instanceof Player player) {
            CommonCallbacks.onPlayerDeath(player, event.getDrops());
        }
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
            AccessoryHelper.getContainer(player).onAttach(player);
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
                consumer.accept(new SyncTypesPacket());
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
                        SetHiddenPacket.TYPE,
                        SetHiddenPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.C2S::handleSetHidden))
                .playToServer(
                        UseAccessoryPacket.TYPE,
                        UseAccessoryPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.C2S::handleUseAccessory))
                .playToClient(
                        SyncHiddenPacket.TYPE,
                        SyncHiddenPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.S2C::handleSyncHidden))
                .playToClient(
                        SyncStacksPacket.TYPE,
                        SyncStacksPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.S2C::handleSyncStacks))
                .configurationToClient(
                        SyncTypesPacket.TYPE,
                        SyncTypesPacket.CODEC,
                        new MainThreadPayloadHandler<>(OhmegaNetworkingImpl.S2C::handleSyncTypes));
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Ohmega.RELOAD_LISTENER_ID, AccessoryTypeManager.getInstance());
    }
}
