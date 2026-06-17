package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.util.codec.OhmegaByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SyncHiddenPacket(int entityId, int[] indexes, boolean[] values) implements CustomPacketPayload {
    public static final Type<@NonNull SyncHiddenPacket> TYPE = new Type<>(Ohmega.id("sync_hidden"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHiddenPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, inst -> inst.entityId,
            OhmegaByteBufCodecs.VAR_INT_ARRAY, inst -> inst.indexes,
            OhmegaByteBufCodecs.BOOLEAN_ARRAY, inst -> inst.values,
            SyncHiddenPacket::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
