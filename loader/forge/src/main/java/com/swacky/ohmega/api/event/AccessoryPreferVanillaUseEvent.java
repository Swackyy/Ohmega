package com.swacky.ohmega.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryPreferVanillaUseEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryPreferVanillaUseEvent> BUS = EventBus.create(AccessoryPreferVanillaUseEvent.class);

    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryPreferVanillaUseEvent(ItemStack stack, boolean original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
