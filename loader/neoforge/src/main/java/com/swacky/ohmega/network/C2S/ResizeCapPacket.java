package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ResizeCapPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ResizeCapPacket> TYPE = new CustomPacketPayload.Type<>(OhmegaCommon.rl("resize_cap_packet"));

    public static final StreamCodec<ByteBuf, ResizeCapPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ResizeCapPacket decode(@NotNull ByteBuf buf) {
            return new ResizeCapPacket();
        }

        @Override
        public void encode(@NotNull ByteBuf buf, @NotNull ResizeCapPacket packet) {
        }
    };

    public static void handle(ResizeCapPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                player.getData(OhmegaDataAttachments.ACCESSORY_HANDLER.get()).reloadCfg();
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
