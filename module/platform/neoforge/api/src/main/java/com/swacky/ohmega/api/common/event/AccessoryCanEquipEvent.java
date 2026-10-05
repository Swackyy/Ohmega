package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.item.AccessoryContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryCanEquipEvent extends Event {
    public final LivingEntity entity;
    public final ItemStack stack;
    public final AccessoryContext context;
    public boolean returnValue;

    public AccessoryCanEquipEvent(LivingEntity entity, ItemStack stack, AccessoryContext context, boolean original) {
        this.entity = entity;
        this.stack = stack;
        this.context = context;
        this.returnValue = original;
    }
}
