package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

// todo: reorder on forge and neo
public record SetExtensionVisiblePacket(boolean value) implements CustomPacketPayload {
    public static final Type<@NonNull SetExtensionVisiblePacket> TYPE = new Type<>(Ohmega.id("set_extension_visible"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetExtensionVisiblePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, inst -> inst.value,
            SetExtensionVisiblePacket::new
    );

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
