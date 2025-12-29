package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record UseAccessoryPacket(int slot) implements CustomPacketPayload {
    public static final Type<@NonNull UseAccessoryPacket> TYPE = new Type<>(OhmegaCommon.id("use_accessory"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UseAccessoryPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, inst -> inst.slot,
            UseAccessoryPacket::new);

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
