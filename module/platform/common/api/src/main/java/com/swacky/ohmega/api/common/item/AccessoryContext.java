package com.swacky.ohmega.api.common.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

/**
 * Context for when an accessory is equipped or un-equipped, provided for certain methods and events.
 * @apiNote A boolean {@link #isMutateSafe()} is provided to state whether it is generally considered safe to either cancel,
 * or heavily modify changes occurring with the provided context. The value of this does not technically change anything internally
 */
public enum AccessoryContext implements StringRepresentable {
    ADVANCEMENT("advancement", true),
    ATTACH("attach", false),
    COMMAND("command", true),
    CONFIG("config", true),
    DEATH("death", false),
    DISPENSE("dispense", true),
    RESIZE("resize", false),
    USE_HELD("use_held", true),
    SLOT("slot", false),
    SYNC("sync", false),
    UNKNOWN("unknown", false);

    private static final AccessoryContext[] VALUES = values();

    /**
     * Simple {@link Codec} to serialise an instance of this enum to persistent data
     * @apiNote For persistent data storage, a {@link String} is used to represent the stored value,
     * as to avoid creating undefined behaviour on changing API version
     */
    public static final @NonNull Codec<AccessoryContext> CODEC = StringRepresentable.fromEnum(AccessoryContext::values);

    /**
     * Simple {@link StreamCodec} to send an instance of this enum over a network
     * @apiNote For immediate data storage, an {@code int} representation is used (see {@link Enum#ordinal()} to represent the stored value,
     * as it is safe to assume that ordinals will match across client and server as a mismatched major version will already be inherently incompatible
     */
    public static final @NonNull StreamCodec<ByteBuf, AccessoryContext> STREAM_CODEC = ByteBufCodecs.idMapper(
            ordinal -> VALUES[ordinal],
            AccessoryContext::ordinal);

    private final @NonNull String name;
    private final boolean mutateSafe;

    AccessoryContext(@NonNull String name, boolean mutateSafe) {
        this.name = name;
        this.mutateSafe = mutateSafe;
    }



    /**
     * Whether modification of changes with this context is safe
     * @return {@code true} if modifying changes with this context is probably safe, {@code false} otherwise
     */
    public boolean isMutateSafe() {
        return mutateSafe;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
