package com.swacky.ohmega.api.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryIsPiglinSafeEvent {
    Event<AccessoryIsPiglinSafeEvent> EVENT = EventFactory.createArrayBacked(AccessoryIsPiglinSafeEvent.class,
        listeners -> (stack, returnValue) -> {
            for (AccessoryIsPiglinSafeEvent listener : listeners) {
                returnValue = listener.process(stack, returnValue);
            }

            return returnValue;
        }
    );

    boolean process(ItemStack stack, boolean original);
}
