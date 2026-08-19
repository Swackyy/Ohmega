package com.swacky.ohmega.api.common.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryAutoSyncModuloEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryAutoSyncModuloEvent> BUS = EventBus.create(AccessoryAutoSyncModuloEvent.class);

    public final ItemStack stack;
    public byte returnValue;

    public AccessoryAutoSyncModuloEvent(ItemStack stack, byte original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
