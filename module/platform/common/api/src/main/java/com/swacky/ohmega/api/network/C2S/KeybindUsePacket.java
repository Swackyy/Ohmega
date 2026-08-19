package com.swacky.ohmega.api.network.C2S;

import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record KeybindUsePacket(int index) implements CustomPacketPayload {
    public static final Type<@NonNull KeybindUsePacket> TYPE = new Type<>(Ohmega.id("keybind_use"));
    public static final StreamCodec<RegistryFriendlyByteBuf, KeybindUsePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, KeybindUsePacket::index,
            KeybindUsePacket::new);

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
