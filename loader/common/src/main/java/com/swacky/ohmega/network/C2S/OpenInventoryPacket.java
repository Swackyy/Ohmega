package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public final class OpenInventoryPacket implements CustomPacketPayload {
    public static final OpenInventoryPacket INSTANCE = new OpenInventoryPacket();
    public static final Type<@NonNull OpenInventoryPacket> TYPE = new Type<>(Ohmega.id("open_inventory"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenInventoryPacket> CODEC = StreamCodec.unit(INSTANCE);

    private OpenInventoryPacket() {}

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
