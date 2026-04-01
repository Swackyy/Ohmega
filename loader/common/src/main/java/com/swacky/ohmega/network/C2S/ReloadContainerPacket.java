package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public final class ReloadContainerPacket implements CustomPacketPayload {
    public static final ReloadContainerPacket INSTANCE = new ReloadContainerPacket();
    public static final Type<@NonNull ReloadContainerPacket> TYPE = new Type<>(Ohmega.id("reload_container"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadContainerPacket> CODEC = StreamCodec.unit(INSTANCE);

    private ReloadContainerPacket() {}

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
