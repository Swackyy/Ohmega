package com.swacky.ohmega.api.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface AccessoryMobVisibilityEvent {
    Event<AccessoryMobVisibilityEvent> EVENT = EventFactory.createArrayBacked(AccessoryMobVisibilityEvent.class,
        listeners -> (stack, targetingEntity, returnValue) -> {
            for (AccessoryMobVisibilityEvent listener : listeners) {
                returnValue = listener.process(stack, targetingEntity, returnValue);
            }

            return returnValue;
        }
    );

    double process(ItemStack stack, @Nullable Entity targetingEntity, double original);
}
