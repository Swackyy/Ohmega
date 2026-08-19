package com.swacky.ohmega.api.common.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/**
 * Not fired upon auto-syncing, but instead fired to determine whether to auto-sync
 */
public final class AccessoryAutoSyncEvent extends Event {
    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryAutoSyncEvent(ItemStack stack, boolean original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
