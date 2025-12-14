package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class ResizeCapPacket implements CustomPacketPayload {
    public static final Type<@NotNull ResizeCapPacket> TYPE = new Type<>(OhmegaCommon.id("resize_cap_packet"));

    public static final StreamCodec<ByteBuf, ResizeCapPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ResizeCapPacket decode(@NotNull ByteBuf buf) {
            return new ResizeCapPacket();
        }

        @Override
        public void encode(@NotNull ByteBuf buf, @NotNull ResizeCapPacket packet) {
        }
    };

    public static void handle(ResizeCapPacket packet, ServerPlayNetworking.Context context) {
        AccessoryHelper.getContainer(context.player()).reloadCfg();
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
