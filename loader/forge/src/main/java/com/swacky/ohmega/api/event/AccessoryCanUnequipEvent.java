package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryCanUnequipEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryCanUnequipEvent> BUS = EventBus.create(AccessoryCanUnequipEvent.class);

    public final LivingEntity entity;
    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryCanUnequipEvent(LivingEntity entity, ItemStack stack, boolean original) {
        this.entity = entity;
        this.stack = stack;
        this.returnValue = original;
    }
}
