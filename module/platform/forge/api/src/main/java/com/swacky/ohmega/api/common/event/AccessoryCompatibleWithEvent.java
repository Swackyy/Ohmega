package com.swacky.ohmega.api.common.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryCompatibleWithEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryCompatibleWithEvent> BUS = EventBus.create(AccessoryCompatibleWithEvent.class);

    public final ItemStack stack;
    public final ItemStack other;
    public boolean returnValue;

    public AccessoryCompatibleWithEvent(ItemStack stack, ItemStack other, boolean original) {
        this.stack = stack;
        this.other = other;
        this.returnValue = original;
    }
}
