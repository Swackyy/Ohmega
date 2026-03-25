package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public final class OpenAccessoryInventoryPacket implements CustomPacketPayload {
    public static final Type<@NonNull OpenAccessoryInventoryPacket> TYPE = new Type<>(Ohmega.id("open_accessory_inventory"));
    public static final OpenAccessoryInventoryPacket INSTANCE = new OpenAccessoryInventoryPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAccessoryInventoryPacket> CODEC = StreamCodec.unit(INSTANCE);

    private OpenAccessoryInventoryPacket() {}

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
