package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface AccessoryMobVisibilityEvent {
    Event<AccessoryMobVisibilityEvent> EVENT = EventFactory.createArrayBacked(AccessoryMobVisibilityEvent.class,
        listeners -> (stack, targetingEntity, ret) -> {
            for (AccessoryMobVisibilityEvent listener : listeners) {
                ret = listener.process(stack, targetingEntity, ret);
            }

            return ret;
        }
    );

    double process(ItemStack stack, Entity targetingEntity, double original);
}
