package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.network.OhmegaByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

// todo: check if playerId is needed
public record SyncStacksPacket(int playerId, int[] indexes, List<ItemStack> stacks, boolean forceOnEquip) implements CustomPacketPayload {
    public static final Type<@NonNull SyncStacksPacket> TYPE = new Type<>(Ohmega.id("sync_stacks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncStacksPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, inst -> inst.playerId,
            OhmegaByteBufCodecs.INT_ARRAY, inst -> inst.indexes,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, inst -> inst.stacks,
            ByteBufCodecs.BOOL, inst -> inst.forceOnEquip,
            SyncStacksPacket::new);

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
