package com.swacky.ohmega.network.common;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SetVisibilityPacket(int index, boolean value) implements CustomPacketPayload {
    public static final Type<@NonNull SetVisibilityPacket> TYPE = new Type<>(Ohmega.id("set_visibility"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetVisibilityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, inst -> inst.index,
            ByteBufCodecs.BOOL, inst -> inst.value,
            SetVisibilityPacket::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
