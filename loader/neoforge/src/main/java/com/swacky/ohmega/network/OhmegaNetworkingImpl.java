package com.swacky.ohmega.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class OhmegaNetworkingImpl {
    public static final class C2S implements OhmegaNetworking.C2S.Service {
        @Override
        public void send(CustomPacketPayload packet) {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();

            if (connection != null) {
                connection.send(packet);
            } else {
                throw new NullPointerException("Minecraft#getConnection returned a null pointer");
            }
        }
    }

    public static final class S2C implements OhmegaNetworking.S2C.Service {
        @Override
        public void send(ServerPlayer receiver, CustomPacketPayload packet) {
            PacketDistributor.sendToPlayer(receiver, packet);
        }
    }
}
