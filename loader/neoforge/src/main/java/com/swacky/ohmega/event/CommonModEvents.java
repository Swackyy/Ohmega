package com.swacky.ohmega.event;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = OhmegaCommon.MODID)
public class CommonModEvents {
    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(OhmegaCommon.rl("sync_accessory_types"));

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
}
