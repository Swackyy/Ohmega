package com.swacky.ohmega.api.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface AccessoryShouldDropOnDeathEvent {
    Event<AccessoryShouldDropOnDeathEvent> EVENT = EventFactory.createArrayBacked(AccessoryShouldDropOnDeathEvent.class,
        listeners -> (stack, entity, returnValue) -> {
            for (AccessoryShouldDropOnDeathEvent listener : listeners) {
                returnValue = listener.process(stack, entity, returnValue);
            }

            return returnValue;
        }
    );

    boolean process(ItemStack stack, LivingEntity entity, boolean original);
}
