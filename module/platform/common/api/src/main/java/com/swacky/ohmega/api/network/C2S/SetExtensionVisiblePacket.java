package com.swacky.ohmega.api.network.C2S;

import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SetExtensionVisiblePacket(boolean value) implements CustomPacketPayload {
    public static final Type<@NonNull SetExtensionVisiblePacket> TYPE = new Type<>(Ohmega.id("set_extension_visible"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetExtensionVisiblePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetExtensionVisiblePacket::value,
            SetExtensionVisiblePacket::new
    );

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
