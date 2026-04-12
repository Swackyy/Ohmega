package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.EquipContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryCanEquipEvent extends Event {
    public final Entity entity;
    public final ItemStack stack;
    public final EquipContext context;
    public boolean returnValue;

    public AccessoryCanEquipEvent(Entity entity, ItemStack stack, EquipContext context, boolean original) {
        this.entity = entity;
        this.stack = stack;
        this.context = context;
        this.returnValue = original;
    }
}
