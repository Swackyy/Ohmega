package com.swacky.ohmega.network;

import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeCapPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ModNetworking {
    public static SimpleChannel INSTANCE;

    public static void register() {
        SimpleChannel net = ChannelBuilder
                .named(OhmegaCommon.rl("network"))
                .networkProtocolVersion(1)
                .clientAcceptedVersions((status, version) -> true)
                .serverAcceptedVersions((status, version) -> true)
                .simpleChannel();

        net.play().serverbound().addMain(OpenAccessoryInventoryPacket.class, OpenAccessoryInventoryPacket.CODEC, OpenAccessoryInventoryPacket::handle);
        net.play().serverbound().addMain(OpenInventoryPacket.class, OpenInventoryPacket.CODEC, OpenInventoryPacket::handle);
        net.play().serverbound().addMain(ResizeCapPacket.class, ResizeCapPacket.CODEC, ResizeCapPacket::handle);
        net.play().serverbound().addMain(UseAccessoryKbPacket.class, UseAccessoryKbPacket.CODEC, UseAccessoryKbPacket::handle);
        net.play().clientbound().addMain(SyncAccessorySlotsPacket.class, SyncAccessorySlotsPacket.CODEC, SyncAccessorySlotsPacket::handle);
        net.configuration().clientbound().addMain(SyncAccessoryTypesPacket.class, SyncAccessoryTypesPacket.CODEC, SyncAccessoryTypesPacket::handle);

        INSTANCE = net;
    }

    public static <T> void sendToServer(T msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static <T> void send(T msg, ServerPlayer player) {
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
    }

    public static <T> void send(T msg, Connection connection) {
        INSTANCE.send(msg, connection);
    }
}
