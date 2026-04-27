package com.swacky.ohmega.api.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryPreferInventoryTickEvent extends Event {
    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryPreferInventoryTickEvent(ItemStack stack, boolean original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
