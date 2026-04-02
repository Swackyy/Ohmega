package com.swacky.ohmega.network;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.ReloadContainerPacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.Objects;

public final class OhmegaNetworkingImpl {
    private static SimpleChannel channel;

    public static void bootstrap() {
        SimpleChannel net = ChannelBuilder
                .named(Ohmega.id("network"))
                .networkProtocolVersion(1)
                .clientAcceptedVersions((_, _) -> true)
                .serverAcceptedVersions((_, _) -> true)
                .simpleChannel();

        // C2S
        net.play().serverbound().addMain(OpenAccessoryInventoryPacket.class, OpenAccessoryInventoryPacket.CODEC, (_, context) ->
                OhmegaNetworking.C2S.handleOpenAccessoryInventory(Objects.requireNonNull(context.getSender())));
        net.play().serverbound().addMain(ReloadContainerPacket.class, ReloadContainerPacket.CODEC, (_, context) ->
                OhmegaNetworking.C2S.handleReloadContainer(Objects.requireNonNull(context.getSender())));
        net.play().serverbound().addMain(SetHiddenPacket.class, SetHiddenPacket.CODEC, (packet, context) ->
                OhmegaNetworking.C2S.handleSetHidden(packet, Objects.requireNonNull(context.getSender())));
        net.play().serverbound().addMain(UseAccessoryPacket.class, UseAccessoryPacket.CODEC, (packet, context) ->
                OhmegaNetworking.C2S.handleUseAccessory(packet, Objects.requireNonNull(context.getSender())));

        // S2C
        net.play().clientbound().addMain(SyncHiddenPacket.class, SyncHiddenPacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncHidden(packet));
        net.play().clientbound().addMain(SyncStacksPacket.class, SyncStacksPacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncStacks(packet));
        net.configuration().clientbound().addMain(SyncTypesPacket.class, SyncTypesPacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncTypes(packet));
        net.play().clientbound().addMain(SyncUsePacket.class, SyncUsePacket.CODEC, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncUse(packet));

        OhmegaNetworkingImpl.channel = net;
    }

    public static final class C2S implements OhmegaNetworking.C2S.Service {
        @Override
        public void send(CustomPacketPayload packet) {
            OhmegaNetworkingImpl.channel.send(packet, PacketDistributor.SERVER.noArg());
        }
    }

    public static final class S2C implements OhmegaNetworking.S2C.Service {
        @Override
        public void send(ServerPlayer receiver, CustomPacketPayload packet) {
            OhmegaNetworkingImpl.channel.send(packet, PacketDistributor.PLAYER.with(receiver));
        }

        public static void send(Connection connection, CustomPacketPayload packet) {
            OhmegaNetworkingImpl.channel.send(packet, connection);
        }
    }
}
