package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface AccessoryUseEvent {
    Event<AccessoryUseEvent> EVENT = EventFactory.createArrayBacked(AccessoryUseEvent.class,
        listeners -> (entity, stack) -> {
            for (AccessoryUseEvent listener : listeners) {
                if (listener.process(entity, stack)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(LivingEntity entity, ItemStack stack);
}
