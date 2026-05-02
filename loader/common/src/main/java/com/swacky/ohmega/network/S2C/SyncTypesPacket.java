package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.OhmegaByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

public final class SyncTypesPacket implements CustomPacketPayload {
    public static final Type<@NonNull SyncTypesPacket> TYPE = new Type<>(Ohmega.id("sync_types"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTypesPacket> CODEC = StreamCodec.composite(
            OhmegaByteBufCodecs.ACCESSORY_TYPE_COLLECTION, inst -> inst.types,
            SyncTypesPacket::new);

    public final Collection<AccessoryType> types;

    private SyncTypesPacket(Collection<AccessoryType> types) {
        this.types = types;
    }

    public SyncTypesPacket() {
        this(AccessoryTypeManager.getTypes());
    }

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
