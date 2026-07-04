package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A helper class for commands, may be useful to others
 */
public final class CommandHelper {
    public static final @NonNull String CONTEXT_HOVER = context("hover");
    public static final @NonNull String EXCEPTION_ARGUMENT_LIVING = exceptionArgument("living");

    public static final @NonNull SimpleCommandExceptionType EXCEPTION_NO_LIVING = new SimpleCommandExceptionType(
            Component.translatable(EXCEPTION_ARGUMENT_LIVING));

    /**
     * Converts a {@link Entity} to a {@link LivingEntity} if possible, and throws an exception otherwise
     * @param target conversion target
     * @return the {@code target} cast to a {@link LivingEntity} if possible
     * @throws CommandSyntaxException an {@link #EXCEPTION_NO_LIVING} will be thrown if the provided {@code target} is not a {@link LivingEntity}
     */
    public static @NonNull LivingEntity convertLiving(@NonNull Entity target) throws CommandSyntaxException {
        if (target instanceof LivingEntity entity) {
            return entity;
        }

        throw EXCEPTION_NO_LIVING.create();
    }

    /**
     * Converts a list of entities to living entities if possible.
     * Does not throw, and instead will just not add invalid entries to the returned list
     * @param targets conversion targets
     * @return a list of all valid {@link Entity} targets cast to their {@link LivingEntity} versions
     */
    public static @NonNull List<LivingEntity> convertLiving(@NonNull Collection<? extends Entity> targets) {
        List<LivingEntity> list = new ArrayList<>(targets.size());

        for (Entity target : targets) {
            if (target instanceof LivingEntity entity) {
                list.add(entity);
            }
        }

        return list;
    }

    /**
     * Internally constructs a new {@link Builder}
     * @return the newly constructed builder
     */
    private static @NonNull Builder builder() {
        return new Builder()
                .add("command")
                .add(Ohmega.MODID);
    }

    /**
     * Creates a translation key for a general top-level contextual hint with the provided string specifier as the key suffix
     * @param suffix a final suffix to end the key with
     * @return the full string key for the context translation
     */
    public static @NonNull String context(@NonNull String suffix) {
        return builder().add("context").add(suffix).toString();
    }

    /**
     * Creates a translation key for a general top-level exception with the provided string specifier as the key suffix
     * @param suffix a final suffix to end the key with
     * @return the full string key for the exception translation
     */
    public static @NonNull String exception(@NonNull String suffix) {
        return builder().add("exception").add(suffix).toString();
    }

    /**
     * Performs the same thing as {@link #exception(String)} but is for command arguments specifically
     * @param suffix a final suffix to end the key with
     * @return the full string key for the exception translation
     */
    public static @NonNull String exceptionArgument(@NonNull String suffix) {
        return exception("argument." + suffix);
    }

    /**
     * Creates a new builder for a command, given the command's string identifier
     * @param command a unique key for this command
     * @return a translation key builder for this command
     */
    public static @NonNull Builder command(@NonNull String command) {
        return builder().add(command);
    }

    /**
     * Easily usable translation key builder for commands
     */
    public static final class Builder {
        private final @NonNull List<String> components = new ArrayList<>();

        /**
         * Adds some general string to the output
         * @param component string to add
         * @return this builder instance
         */
        public @NonNull Builder add(@NonNull String component) {
            components.add(component);
            return this;
        }

        /**
         * Finishes the builder with a feedback key given a suffix
         * @param suffix a final suffix to end the key with
         * @return the final translation key for this feedback message
         */
        public @NonNull String feedback(@NonNull String suffix) {
            return add("feedback").add(suffix).toString();
        }

        /**
         * Finishes the builder with a feedback key
         * @return the final translation key for this feedback message
         */
        public @NonNull String feedback() {
            return add("feedback").toString();
        }

        /**
         * Finishes the builder with an exception key given a suffix
         * @param suffix a final suffix to end the key with
         * @return the final translation key for this exception message
         */
        public @NonNull String exception(@NonNull String suffix) {
            return add("exception").add(suffix).toString();
        }

        /**
         * Finishes the builder with an exception key
         * @return the final translation key for this exception message
         */
        public @NonNull String exception() {
            return add("exception").toString();
        }

        /**
         * Converts the builder to a string by joining all translation key components with {@code .} characters
         * @return the translation key represented by this builder
         */
        @Override
        public @NonNull String toString() {
            return String.join(".", components);
        }
    }
}