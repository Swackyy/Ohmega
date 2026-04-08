package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryMobVisibilityEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryMobVisibilityEvent> BUS = EventBus.create(AccessoryMobVisibilityEvent.class);

    public final ItemStack stack;
    public final Entity targetingEntity;
    public double returnValue;

    public AccessoryMobVisibilityEvent(ItemStack stack, Entity targetingEntity, double original) {
        this.stack = stack;
        this.targetingEntity = targetingEntity;
        this.returnValue = original;
    }
}
