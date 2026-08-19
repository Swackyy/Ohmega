package com.swacky.ohmega.api.common.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryAutoSyncEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryAutoSyncEvent> BUS = EventBus.create(AccessoryAutoSyncEvent.class);

    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryAutoSyncEvent(ItemStack stack, boolean original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
