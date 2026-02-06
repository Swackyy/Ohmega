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
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = OhmegaCommon.MODID)
public class CommonModEvents {
    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(OhmegaCommon.rl("sync_accessory_types"));

    @SubscribeEvent
    public static void onRegisterConfigTasks(OnGameConfigurationEvent event) {
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
    public static void onRegisterNetwork(RegisterPayloadHandlerEvent event) {
        event.registrar(OhmegaCommon.MODID)
                .versioned("1.0")
                .play(
                        OpenAccessoryInventoryPacket.ID,
                        OpenAccessoryInventoryPacket::new,
                        builder -> builder.server(OhmegaNetworkingImpl.C2S::handleOpenAccessoryInventory))
                .play(
                        OpenInventoryPacket.ID,
                        OpenInventoryPacket::new,
                        builder -> builder.server(OhmegaNetworkingImpl.C2S::handleOpenInventory))
                .play(
                        ResizeContainerPacket.ID,
                        ResizeContainerPacket::new,
                        builder -> builder.server(OhmegaNetworkingImpl.C2S::handleResizeContainer))
                .play(
                        UseAccessoryPacket.ID,
                        UseAccessoryPacket::new,
                        builder -> builder.server(OhmegaNetworkingImpl.C2S::handleUseAccessory))
                .play(
                        SyncAccessorySlotsPacket.ID,
                        SyncAccessorySlotsPacket::new,
                        builder -> builder.client(OhmegaNetworkingImpl.S2C::handleSyncAccessorySlots));
    }
}
