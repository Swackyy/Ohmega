package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.util.codec.OhmegaByteBufCodecs;
import com.swacky.ohmega.common.Ohmega;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record SyncSlotsPacket(Action action, int entityId, int[] data, Optional<AccessoryType> accessoryType, EquipContext context) implements CustomPacketPayload {
    public static final Type<@NonNull SyncSlotsPacket> TYPE = new Type<>(Ohmega.id("sync_slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSlotsPacket> CODEC = StreamCodec.composite(
            Action.STREAM_CODEC, SyncSlotsPacket::action,
            ByteBufCodecs.VAR_INT, SyncSlotsPacket::entityId,
            OhmegaByteBufCodecs.VAR_INT_ARRAY, SyncSlotsPacket::data,
            ByteBufCodecs.optional(AccessoryType.STREAM_CODEC), SyncSlotsPacket::accessoryType,
            EquipContext.STREAM_CODEC, SyncSlotsPacket::context,
            SyncSlotsPacket::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Action {
        CLEAR,
        CLEAR_ALL,
        DEFAULT,
        INHERIT,
        INSERT,
        REMOVE,
        SET;

        public static final @NonNull StreamCodec<ByteBuf, Action> STREAM_CODEC = ByteBufCodecs.idMapper(
                ordinal -> Action.values()[ordinal],
                Action::ordinal);
    }
}
