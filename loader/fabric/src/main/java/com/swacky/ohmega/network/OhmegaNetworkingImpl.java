package com.swacky.ohmega.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class OhmegaNetworkingImpl {
    public static final class C2S implements OhmegaNetworking.C2S.Service {
        @Override
        public void send(CustomPacketPayload packet) {
            ClientPlayNetworking.send(packet);
        }
    }

    public static final class S2C implements OhmegaNetworking.S2C.Service {
        @Override
        public void send(ServerPlayer receiver, CustomPacketPayload packet) {
            ServerPlayNetworking.send(receiver, packet);
        }
    }
}
