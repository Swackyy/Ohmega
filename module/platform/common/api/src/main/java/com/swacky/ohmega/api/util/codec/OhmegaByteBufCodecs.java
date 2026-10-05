package com.swacky.ohmega.api.util.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

/**
 * Contains some general use {@link StreamCodec}s that Ohmega may use internally,
 * but is placed in the {@code api} package in case they may be otherwise useful
 */
public final class OhmegaByteBufCodecs {
    /**
     * Codec for an array of boolean primitives
     */
    public static final @NonNull StreamCodec<ByteBuf, boolean[]> BOOLEAN_ARRAY = StreamCodec.of(
            (buf, values) -> {
                VarInt.write(buf, values.length);

                for (boolean value : values) {
                    buf.writeBoolean(value);
                }
            }, buf -> {
                int size = VarInt.read(buf);
                boolean[] values = new boolean[size];

                for (int i = 0; i < size; i++) {
                    values[i] = buf.readBoolean();
                }

                return values;
            }
    );

    /**
     * Codec for an array of {@link VarInt}s
     */
    public static final @NonNull StreamCodec<ByteBuf, int[]> VAR_INT_ARRAY = StreamCodec.of(
            (buf, values) -> {
                VarInt.write(buf, values.length);

                for (int value : values) {
                    VarInt.write(buf, value);
                }
            }, buf -> {
                int size = VarInt.read(buf);
                int[] values = new int[size];

                for (int i = 0; i < size; i++) {
                    values[i] = VarInt.read(buf);
                }

                return values;
            }
    );
}
