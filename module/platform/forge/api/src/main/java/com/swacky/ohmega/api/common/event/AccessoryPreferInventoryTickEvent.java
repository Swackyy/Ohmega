package com.swacky.ohmega.api.common.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryPreferInventoryTickEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryPreferInventoryTickEvent> BUS = EventBus.create(AccessoryPreferInventoryTickEvent.class);

    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryPreferInventoryTickEvent(ItemStack stack, boolean original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
