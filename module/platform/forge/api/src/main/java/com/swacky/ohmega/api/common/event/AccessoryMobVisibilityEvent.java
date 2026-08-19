package com.swacky.ohmega.api.common.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class AccessoryMobVisibilityEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryMobVisibilityEvent> BUS = EventBus.create(AccessoryMobVisibilityEvent.class);

    public final ItemStack stack;
    public final @Nullable Entity targetingEntity;
    public double returnValue;

    public AccessoryMobVisibilityEvent(ItemStack stack, @Nullable Entity targetingEntity, double original) {
        this.stack = stack;
        this.targetingEntity = targetingEntity;
        this.returnValue = original;
    }
}
