package com.swacky.ohmega.api.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryCanUnequipEvent extends Event {
    public final LivingEntity entity;
    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryCanUnequipEvent(LivingEntity entity, ItemStack stack, boolean original) {
        this.entity = entity;
        this.stack = stack;
        this.returnValue = original;
    }
}
