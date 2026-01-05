package com.swacky.ohmega.network.S2C;

import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public final class SyncAccessoryTypesPacket implements CustomPacketPayload {
    public static final Type<@NonNull SyncAccessoryTypesPacket> TYPE = new Type<>(OhmegaCommon.rl("sync_accessory_types"));
    public static final StreamCodec<FriendlyByteBuf, SyncAccessoryTypesPacket> CODEC = StreamCodec.composite(
            AccessoryType.SET_STREAM_CODEC, inst -> inst.types,
            SyncAccessoryTypesPacket::new);

    public final ImmutableSet<AccessoryType> types;

    private SyncAccessoryTypesPacket(ImmutableSet<AccessoryType> types) {
        this.types = types;
    }

    public SyncAccessoryTypesPacket() {
        this(AccessoryTypeManager.getInstance().getTypes());
    }

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
