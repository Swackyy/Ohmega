package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public final class ReloadDataPacket implements CustomPacketPayload {
    public static final ReloadDataPacket INSTANCE = new ReloadDataPacket();
    public static final Type<@NonNull ReloadDataPacket> TYPE = new Type<>(Ohmega.id("reload_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadDataPacket> CODEC = StreamCodec.unit(INSTANCE);

    private ReloadDataPacket() {}

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
