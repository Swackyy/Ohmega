package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record SyncAccessorySlotsPacket(int playerId, int[] indexes, List<ItemStack> stacks) implements CustomPacketPayload {
    public static final Type<@NonNull SyncAccessorySlotsPacket> TYPE = new Type<>(OhmegaCommon.rl("sync_accessory_slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAccessorySlotsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, inst -> inst.playerId,
            new StreamCodec<ByteBuf, int[]>() {
                public int @NonNull [] decode(@NonNull ByteBuf buf) {
                    int size = VarInt.read(buf);
                    int[] values = new int[size];

                    for (int i = 0; i < size; i++) {
                        values[i] = VarInt.read(buf);
                    }
                    return values;
                }

                public void encode(@NonNull ByteBuf buf, int @NonNull [] values) {
                    VarInt.write(buf, values.length);

                    for (int value : values) {
                        VarInt.write(buf, value);
                    }
                }
            }, inst -> inst.indexes,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, inst -> inst.stacks,
            SyncAccessorySlotsPacket::new);

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }
}
