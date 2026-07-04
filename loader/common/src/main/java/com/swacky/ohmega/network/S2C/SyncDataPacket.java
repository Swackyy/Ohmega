package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SyncDataPacket(int entityId, AccessoryData data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NonNull SyncDataPacket> TYPE = new CustomPacketPayload.Type<>(Ohmega.id("sync_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDataPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncDataPacket::entityId,
            AccessoryData.STREAM_CODEC, SyncDataPacket::data,
            SyncDataPacket::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
