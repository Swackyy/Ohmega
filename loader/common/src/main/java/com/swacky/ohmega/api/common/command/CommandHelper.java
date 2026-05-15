package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public final class CommandHelper {
    public static final String CONTEXT_HOVER = context("hover");
    public static final String EXCEPTION_ARGUMENT_LIVING_ONLY = exceptionArgument("living");

    public static final SimpleCommandExceptionType EXCEPTION_LIVING = new SimpleCommandExceptionType(Component.translatable(EXCEPTION_ARGUMENT_LIVING_ONLY));

    public static LivingEntity convertLiving(Entity target) throws CommandSyntaxException {
        if (target instanceof LivingEntity entity) {
            return entity;
        }

        throw EXCEPTION_LIVING.create();
    }

    public static List<LivingEntity> convertLiving(List<Entity> targets) throws CommandSyntaxException {
        List<LivingEntity> ret = new ArrayList<>(targets.size());

        for (Entity target : targets) {
            if (target instanceof LivingEntity entity) {
                ret.add(entity);
            } else {
                throw EXCEPTION_LIVING.create();
            }
        }

        return ret;
    }

    private static Builder builder() {
        return new Builder()
                .add("command")
                .add(Ohmega.MODID);
    }

    public static String context(String suffix) {
        return builder().add("context").add(suffix).toString();
    }

    public static String exception(String suffix) {
        return builder().add("exception").add(suffix).toString();
    }

    public static String exceptionArgument(String suffix) {
        return exception("argument." + suffix);
    }

    public static Builder command(String command) {
        return builder().add(command);
    }

    public static final class Builder {
        private final List<String> components = new ArrayList<>(2);

        public Builder add(String component) {
            components.add(component);
            return this;
        }

        public String feedback(String suffix) {
            return add("feedback").add(suffix).toString();
        }

        public String feedback() {
            return add("feedback").toString();
        }

        public String exception(String suffix) {
            return add("exception").add(suffix).toString();
        }

        public String exception() {
            return add("exception").toString();
        }

        @Override
        public String toString() {
            return String.join(".", components);
        }
    }
}