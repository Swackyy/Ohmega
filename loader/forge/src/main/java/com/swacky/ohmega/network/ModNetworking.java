package com.swacky.ohmega.network;

import com.swacky.ohmega.network.C2S.*;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;

public class ModNetworking {
    public static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = ChannelBuilder
                .named(OhmegaCommon.rl("network"))
                .networkProtocolVersion(1)
                .clientAcceptedVersions((status, version) -> true)
                .serverAcceptedVersions((status, version) -> true)
                .simpleChannel();
        net.messageBuilder(OpenAccessoryInventoryPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenAccessoryInventoryPacket::new)
                .encoder(OpenAccessoryInventoryPacket::toBytes)
                .consumerMainThread(OpenAccessoryInventoryPacket::handle)
                .add();
        net.messageBuilder(OpenInventoryPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenInventoryPacket::new)
                .encoder(OpenInventoryPacket::toBytes)
                .consumerMainThread(OpenInventoryPacket::handle)
                .add();
        net.messageBuilder(ResizeCapPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ResizeCapPacket::new)
                .encoder(ResizeCapPacket::toBytes)
                .consumerMainThread(ResizeCapPacket::handle)
                .add();
        net.messageBuilder(UseAccessoryKbPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UseAccessoryKbPacket::new)
                .encoder(UseAccessoryKbPacket::toBytes)
                .consumerMainThread(UseAccessoryKbPacket::handle)
                .add();
        net.messageBuilder(SyncAccessorySlotsPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncAccessorySlotsPacket::new)
                .encoder(SyncAccessorySlotsPacket::toBytes)
                .consumerMainThread(SyncAccessorySlotsPacket::handle)
                .add();
        net.messageBuilder(SyncAccessoryTypesPacket.class, id(), NetworkDirection.CONFIGURATION_TO_CLIENT)
                .decoder(SyncAccessoryTypesPacket::new)
                .encoder(SyncAccessoryTypesPacket::toBytes)
                .consumerMainThread(SyncAccessoryTypesPacket::handle)
                .add();
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
