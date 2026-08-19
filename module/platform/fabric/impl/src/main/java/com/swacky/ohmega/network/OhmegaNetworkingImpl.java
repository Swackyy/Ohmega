package com.swacky.ohmega.network;

import com.swacky.ohmega.api.network.OhmegaNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unused")
public final class OhmegaNetworkingImpl implements OhmegaNetworking.Service {
    @Override
    public void sendC2S(CustomPacketPayload packet) {
        ClientPlayNetworking.send(packet);
    }

    @Override
    public void sendS2C(ServerPlayer receiver, CustomPacketPayload packet) {
        ServerPlayNetworking.send(receiver, packet);
    }
}
