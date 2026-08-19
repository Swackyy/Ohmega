package com.swacky.ohmega.api.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import org.jspecify.annotations.Nullable;

public final class AccessoryMobVisibilityEvent extends Event {
    public final ItemStack stack;
    public final @Nullable Entity targetingEntity;
    public double returnValue;

    public AccessoryMobVisibilityEvent(ItemStack stack, @Nullable Entity targetingEntity, double original) {
        this.stack = stack;
        this.targetingEntity = targetingEntity;
        this.returnValue = original;
    }
}
