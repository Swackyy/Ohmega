package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class OpenInventoryPacket implements CustomPacketPayload {
    public static final Type<OpenInventoryPacket> TYPE = new Type<>(OhmegaCommon.rl("open_inventory_packet"));

    public static final StreamCodec<ByteBuf, OpenInventoryPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenInventoryPacket decode(@NotNull ByteBuf buf) {
            return new OpenInventoryPacket();
        }

        @Override
        public void encode(@NotNull ByteBuf buf, @NotNull OpenInventoryPacket packet) {
        }
    };

    public static void handle(OpenInventoryPacket packet, ServerPlayNetworking.Context context) {
        context.player().doCloseContainer();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
