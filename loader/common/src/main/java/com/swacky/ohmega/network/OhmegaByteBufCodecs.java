package com.swacky.ohmega.network;

import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public class OhmegaByteBufCodecs {
    public static final StreamCodec<ByteBuf, boolean[]> BOOLEAN_ARRAY = new StreamCodec<>() {
        public boolean @NonNull [] decode(@NonNull ByteBuf buf) {
            int size = VarInt.read(buf);
            boolean[] values = new boolean[size];

            for (int i = 0; i < size; i++) {
                values[i] = buf.readBoolean();
            }

            return values;
        }

        public void encode(@NonNull ByteBuf buf, boolean @NonNull [] values) {
            VarInt.write(buf, values.length);

            for (boolean value : values) {
                buf.writeBoolean(value);
            }
        }
    };

    public static final StreamCodec<ByteBuf, int[]> INT_ARRAY = new StreamCodec<>() {
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
    };

    public static final StreamCodec<FriendlyByteBuf, ImmutableSet<AccessoryType>> ACCESSORY_TYPE_SET = new StreamCodec<>() {
        @Override
        public @NonNull ImmutableSet<AccessoryType> decode(@NonNull FriendlyByteBuf buf) {
            int size = VarInt.read(buf);
            ImmutableSet.Builder<AccessoryType> builder = ImmutableSet.builderWithExpectedSize(size);

            for (int i = 0; i < size; i++) {
                builder.add(AccessoryType.STREAM_CODEC.decode(buf));
            }

            return builder.build();
        }

        @Override
        public void encode(@NonNull FriendlyByteBuf buf, @NonNull ImmutableSet<AccessoryType> values) {
            VarInt.write(buf, values.size());

            for (AccessoryType value : values) {
                AccessoryType.STREAM_CODEC.encode(buf, value);
            }
        }
    };
}
