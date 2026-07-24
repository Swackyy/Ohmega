package com.swacky.ohmega.network;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.network.C2S.KeybindUsePacket;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncDataPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncKeybindUsePacket;
import com.swacky.ohmega.network.S2C.SyncSlotsPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.Objects;

public final class OhmegaNetworkingImpl implements OhmegaNetworking.Service {
    public static SimpleChannel channel;

    @Override
    public void sendC2S(CustomPacketPayload packet) {
        channel.send(packet, PacketDistributor.SERVER.noArg());
    }

    @Override
    public void sendS2C(ServerPlayer receiver, CustomPacketPayload packet) {
        channel.send(packet, PacketDistributor.PLAYER.with(receiver));
    }

    public static void bootstrap() {
        SimpleChannel net = ChannelBuilder
                .named(Ohmega.id("network"))
                .networkProtocolVersion(1)
                .clientAcceptedVersions((_, _) -> true)
                .serverAcceptedVersions((_, _) -> true)
                .simpleChannel();

        // C2S
        net.play().serverbound().addMain(KeybindUsePacket.class, KeybindUsePacket.CODEC, (packet, context) ->
                OhmegaNetworking.C2S.handleKeybindUse(packet, Objects.requireNonNull(context.getSender())));
        net.play().serverbound().addMain(SetExtensionVisiblePacket.class, SetExtensionVisiblePacket.CODEC, (packet, context) ->
                OhmegaNetworking.C2S.handleSetExtensionVisible(packet, Objects.requireNonNull(context.getSender())));
        net.play().serverbound().addMain(SetHiddenPacket.class, SetHiddenPacket.CODEC, (packet, context) ->
                OhmegaNetworking.C2S.handleSetHidden(packet, Objects.requireNonNull(context.getSender())));

        // S2C
        net.play().clientbound().addMain(SyncDataPacket.class, SyncDataPacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncData(packet));
        net.play().clientbound().addMain(SyncHiddenPacket.class, SyncHiddenPacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncHidden(packet));
        net.play().clientbound().addMain(SyncKeybindUsePacket.class, SyncKeybindUsePacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncKeybindUse(packet));
        net.play().clientbound().addMain(SyncSlotsPacket.class, SyncSlotsPacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncSlots(packet));
        net.play().clientbound().addMain(SyncStacksPacket.class, SyncStacksPacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncStacks(packet));
        net.configuration().clientbound().addMain(SyncTypesPacket.class, SyncTypesPacket.CODEC, (packet, context) -> {
            if (context.getConnection().getPacketListener() instanceof ClientConfigurationPacketListenerImpl listener) {
                OhmegaNetworking.S2C.handleSyncTypes(packet, listener.receivedRegistries);
            }
        });

        OhmegaNetworkingImpl.channel = net.build();
    }

    public static void send(Connection connection, CustomPacketPayload packet) {
        channel.send(packet, connection);
    }
}
