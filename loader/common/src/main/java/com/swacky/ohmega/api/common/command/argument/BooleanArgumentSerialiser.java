package com.swacky.ohmega.api.common.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

/**
 * Serialiser template class for single {@code boolean} carrying command {@link ArgumentType}s
 * @param <T> the {@link ArgumentType} to serialise
 */
public final class BooleanArgumentSerialiser<T extends ArgumentType<?>> implements ArgumentTypeInfo<T, BooleanArgumentSerialiser.Template<T>> {
    private final @NonNull Boolean2ObjectFunction<T> factory;
    private final @NonNull Predicate<T> flagGetter;

    /**
     * Serialiser constructor, should probably be instantiated as a {@code public static final} field and referenced in registration
     * @param factory constructor function reference
     * @param flagGetter {@code boolean} flag state getter
     */
    public BooleanArgumentSerialiser(@NonNull Boolean2ObjectFunction<T> factory, @NonNull Predicate<T> flagGetter) {
        this.factory = factory;
        this.flagGetter = flagGetter;
    }

    @Override
    public void serializeToNetwork(@NonNull Template template, @NonNull FriendlyByteBuf buf) {
        buf.writeBoolean(template.flag);
    }

    @Override
    public @NonNull Template<T> deserializeFromNetwork(@NonNull FriendlyByteBuf buf) {
        return new Template<>(this, buf.readBoolean());
    }

    @Override
    public void serializeToJson(@NonNull Template<T> template, @NonNull JsonObject json) {
        json.addProperty("flag", template.flag);
    }

    @Override
    public @NonNull Template<T> unpack(@NonNull T argument) {
        return new Template<>(this, flagGetter.test(argument));
    }

    /**
     * Implementation of {@link ArgumentTypeInfo.Template}
     * @param <T> the {@link ArgumentType} to serialise
     */
    public static final class Template<T extends ArgumentType<?>> implements ArgumentTypeInfo.Template<T> {
        private final @NonNull BooleanArgumentSerialiser<T> serialiser;
        private final boolean flag;

        /**
         * Template constructor, used in {@link BooleanArgumentSerialiser} internally
         * @param serialiser parent {@link BooleanArgumentSerialiser} instance
         * @param flag {@code boolean} flag data state
         */
        private Template(@NonNull BooleanArgumentSerialiser<T> serialiser, boolean flag) {
            this.serialiser = serialiser;
            this.flag = flag;
        }

        @Override
        public @NonNull T instantiate(@NonNull CommandBuildContext context) {
            return serialiser.factory.apply(flag);
        }

        @Override
        public @NonNull ArgumentTypeInfo<T, ?> type() {
            return serialiser;
        }
    }
}
