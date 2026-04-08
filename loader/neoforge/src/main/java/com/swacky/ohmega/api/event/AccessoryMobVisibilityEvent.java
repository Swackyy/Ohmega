package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryMobVisibilityEvent extends Event {
    public final ItemStack stack;
    public final Entity targetingEntity;
    public double returnValue;

    public AccessoryMobVisibilityEvent(ItemStack stack, Entity targetingEntity, double original) {
        this.stack = stack;
        this.targetingEntity = targetingEntity;
        this.returnValue = original;
    }
}
