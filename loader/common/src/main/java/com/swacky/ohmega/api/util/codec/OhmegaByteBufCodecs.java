package com.swacky.ohmega.api.util.codec;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains some general use {@link StreamCodec}s that Ohmega may use internally,
 * but is placed in the {@code api} package in case they may be otherwise useful
 */
public final class OhmegaByteBufCodecs {
    /**
     * Codec for an array of boolean primitives
     */
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

    /**
     * Codec for an array of {@link VarInt}s
     */
    public static final StreamCodec<ByteBuf, int[]> VAR_INT_ARRAY = new StreamCodec<>() {
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

    /**
     * Codec for any general collection of {@link AccessoryType}s
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, Collection<AccessoryType>> ACCESSORY_TYPE_COLLECTION = new StreamCodec<>() {
        @Override
        public @NonNull Collection<AccessoryType> decode(@NonNull RegistryFriendlyByteBuf buf) {
            int size = VarInt.read(buf);
            Set<AccessoryType> map = new HashSet<>(size);

            for (int i = 0; i < size; i++) {
                map.add(AccessoryType.STREAM_CODEC.decode(buf));
            }

            return map;
        }

        @Override
        public void encode(@NonNull RegistryFriendlyByteBuf buf, @NonNull Collection<AccessoryType> values) {
            VarInt.write(buf, values.size());

            for (AccessoryType value : values) {
                AccessoryType.STREAM_CODEC.encode(buf, value);
            }
        }
    };
}
