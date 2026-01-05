package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public final class ResizeContainerPacket implements CustomPacketPayload {
    public static final Type<@NonNull ResizeContainerPacket> TYPE = new Type<>(OhmegaCommon.rl("resize_container"));
    public static final ResizeContainerPacket INSTANCE = new ResizeContainerPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, ResizeContainerPacket> CODEC = StreamCodec.unit(INSTANCE);

    private ResizeContainerPacket() {}

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
