package com.swacky.ohmega.api.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryShouldDropOnDeathEvent extends Event {
    public final ItemStack stack;
    public final LivingEntity entity;
    public boolean returnValue;

    public AccessoryShouldDropOnDeathEvent(ItemStack stack, LivingEntity entity, boolean original) {
        this.stack = stack;
        this.entity = entity;
        this.returnValue = original;
    }
}
