package com.swacky.ohmega.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class OhmegaNetworkingImpl implements OhmegaNetworking.Service {
    @Override
    public void sendC2S(CustomPacketPayload packet) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();

        if (connection != null) {
            connection.send(packet);
        } else {
            throw new NullPointerException("Minecraft#getConnection returned a null pointer");
        }
    }

    @Override
    public void sendS2C(ServerPlayer receiver, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(receiver, packet);
    }
}
