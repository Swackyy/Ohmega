package com.swacky.ohmega.api.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryShouldDropOnDeathEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryShouldDropOnDeathEvent> BUS = EventBus.create(AccessoryShouldDropOnDeathEvent.class);

    public final ItemStack stack;
    public final LivingEntity entity;
    public boolean returnValue;

    public AccessoryShouldDropOnDeathEvent(ItemStack stack, LivingEntity entity, boolean original) {
        this.stack = stack;
        this.entity = entity;
        this.returnValue = original;
    }
}
