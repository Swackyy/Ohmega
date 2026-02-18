package com.swacky.ohmega.event;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = OhmegaCommon.MODID)
public class CommonModEvents {
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
