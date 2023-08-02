package com.swacky.ohmega.network;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.C2S.*;
import com.swacky.ohmega.network.S2C.SyncAccessoriesPacket;
import com.swacky.ohmega.network.S2C.SyncActivePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    public static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Ohmega.MODID, "network"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        net.messageBuilder(OpenAccessoryGuiPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenAccessoryGuiPacket::new)
                .encoder(OpenAccessoryGuiPacket::toBytes)
                .consumerMainThread(OpenAccessoryGuiPacket::handle)
                .add();
        net.messageBuilder(OpenInventoryPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenInventoryPacket::new)
                .encoder(OpenInventoryPacket::toBytes)
                .consumerMainThread(OpenInventoryPacket::handle)
                .add();
        net.messageBuilder(SyncAccessoriesPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncAccessoriesPacket::new)
                .encoder(SyncAccessoriesPacket::toBytes)
                .consumerMainThread(SyncAccessoriesPacket::handle)
                .add();
        net.messageBuilder(UseAccessoryKbPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UseAccessoryKbPacket::new)
                .encoder(UseAccessoryKbPacket::toBytes)
                .consumerMainThread(UseAccessoryKbPacket::handle)
                .add();
        net.messageBuilder(SyncActivePacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncActivePacket::new)
                .encoder(SyncActivePacket::toBytes)
                .consumerMainThread(SyncActivePacket::handle)
                .add();
        INSTANCE = net;
    }

    public static <T> void sendToServer(T msg) {
        INSTANCE.sendToServer(msg);
    }

    public static <T> void sendTo(T msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
