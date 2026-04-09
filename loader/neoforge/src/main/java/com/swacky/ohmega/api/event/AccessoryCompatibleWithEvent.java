package com.swacky.ohmega.api.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/**
 * Not fired upon auto-syncing, but instead fired to determine whether to auto-sync
 */
public final class AccessoryCompatibleWithEvent extends Event {
    public final ItemStack stack;
    public final ItemStack other;
    public boolean returnValue;

    public AccessoryCompatibleWithEvent(ItemStack stack, ItemStack other, boolean original) {
        this.stack = stack;
        this.other = other;
        this.returnValue = original;
    }
}
