package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class CommandHelper {
    public static final @NonNull String CONTEXT_HOVER = context("hover");
    public static final @NonNull String EXCEPTION_ARGUMENT_LIVING_ONLY = exceptionArgument("living");

    public static final @NonNull SimpleCommandExceptionType EXCEPTION_LIVING = new SimpleCommandExceptionType(Component.translatable(EXCEPTION_ARGUMENT_LIVING_ONLY));

    public static @NonNull LivingEntity convertLiving(@NonNull Entity target) throws CommandSyntaxException {
        if (target instanceof LivingEntity entity) {
            return entity;
        }

        throw EXCEPTION_LIVING.create();
    }

    public static @NonNull List<LivingEntity> convertLiving(@NonNull List<Entity> targets) throws CommandSyntaxException {
        List<LivingEntity> list = new ArrayList<>(targets.size());

        for (Entity target : targets) {
            if (target instanceof LivingEntity entity) {
                list.add(entity);
            } else {
                throw EXCEPTION_LIVING.create();
            }
        }

        return list;
    }

    private static @NonNull Builder builder() {
        return new Builder()
                .add("command")
                .add(Ohmega.MODID);
    }

    public static @NonNull String context(@NonNull String suffix) {
        return builder().add("context").add(suffix).toString();
    }

    public static @NonNull String exception(@NonNull String suffix) {
        return builder().add("exception").add(suffix).toString();
    }

    public static @NonNull String exceptionArgument(@NonNull String suffix) {
        return exception("argument." + suffix);
    }

    public static @NonNull Builder command(@NonNull String command) {
        return builder().add(command);
    }

    public static final class Builder {
        private final @NonNull List<String> components = new ArrayList<>(2);

        public @NonNull Builder add(@NonNull String component) {
            components.add(component);
            return this;
        }

        public @NonNull String feedback(@NonNull String suffix) {
            return add("feedback").add(suffix).toString();
        }

        public @NonNull String feedback() {
            return add("feedback").toString();
        }

        public @NonNull String exception(@NonNull String suffix) {
            return add("exception").add(suffix).toString();
        }

        public @NonNull String exception() {
            return add("exception").toString();
        }

        @Override
        public @NonNull String toString() {
            return String.join(".", components);
        }
    }
}