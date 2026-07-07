package com.swacky.ohmega.api.common.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

// todo: possibly rename?
/**
 * Context for when an accessory is equipped or un-equipped, provided for certain methods and events.
 * @apiNote A boolean {@link #isMutateSafe()} is provided to state whether it is generally considered safe to either cancel,
 * or heavily modify changes occurring with the provided context. The value of this does not technically change anything internally
 */
public enum EquipContext {
    ATTACH(false),
    COMMAND(true),
    CONFIG(true),
    DEATH(false),
    DISPENSE(true),
    RESIZE(false),
    USE_HELD(true),
    SLOT(false),
    SYNC(false),
    UNKNOWN(false);

    /**
     * Simple {@link StreamCodec} to send an instance of this enum over a network
     */
    public static final @NonNull StreamCodec<ByteBuf, EquipContext> STREAM_CODEC = ByteBufCodecs.idMapper(
            ordinal -> EquipContext.values()[ordinal],
            EquipContext::ordinal);

    private final boolean mutateSafe;

    EquipContext(boolean mutateSafe) {
        this.mutateSafe = mutateSafe;
    }

    /**
     * Whether modification of changes with this context is safe
     * @return {@code true} if modifying changes with this context is probably safe, {@code false} otherwise
     */
    public boolean isMutateSafe() {
        return mutateSafe;
    }
}
