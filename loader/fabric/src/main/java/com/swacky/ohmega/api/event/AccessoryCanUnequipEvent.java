package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCanUnequipEvent {
    Event<AccessoryCanUnequipEvent> EVENT = EventFactory.createArrayBacked(AccessoryCanUnequipEvent.class,
        listeners -> (entity, stack, returnValue) -> {
            for (AccessoryCanUnequipEvent listener : listeners) {
                returnValue = listener.process(entity, stack, returnValue);
            }

            return returnValue;
        }
    );

    boolean process(LivingEntity entity, ItemStack stack, boolean original);
}
