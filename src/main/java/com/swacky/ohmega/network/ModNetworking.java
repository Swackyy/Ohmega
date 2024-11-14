package com.swacky.ohmega.network;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.C2S.*;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotPacket;
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
                .named(Ohmega.rl("network"))
                .networkProtocolVersion(1)
                .clientAcceptedVersions((status, version) -> true)
                .serverAcceptedVersions((status, version) -> true)
                .simpleChannel();
        net.messageBuilder(ResizeCapPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ResizeCapPacket::new)
                .encoder(ResizeCapPacket::toBytes)
                .consumerMainThread(ResizeCapPacket::handle)
                .add();
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
        net.messageBuilder(SyncAccessorySlotPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncAccessorySlotPacket::new)
                .encoder(SyncAccessorySlotPacket::toBytes)
                .consumerMainThread(SyncAccessorySlotPacket::handle)
                .add();
        net.messageBuilder(UseAccessoryKbPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UseAccessoryKbPacket::new)
                .encoder(UseAccessoryKbPacket::toBytes)
                .consumerMainThread(UseAccessoryKbPacket::handle)
                .add();
        INSTANCE = net;
    }

    public static <T> void sendToServer(T msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static <T> void sendTo(T msg, ServerPlayer player) {
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
    }
}
