package com.swacky.ohmega.api.common.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryIsPiglinSafeEvent extends Event {
    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryIsPiglinSafeEvent(ItemStack stack, boolean original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
