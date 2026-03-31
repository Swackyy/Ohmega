package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SetHiddenPacket(int index, boolean value) implements CustomPacketPayload {
    public static final Type<@NonNull SetHiddenPacket> TYPE = new Type<>(Ohmega.id("set_hidden"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetHiddenPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, inst -> inst.index,
            ByteBufCodecs.BOOL, inst -> inst.value,
            SetHiddenPacket::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
