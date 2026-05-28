package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryAutoSyncEvent {
    Event<AccessoryAutoSyncEvent> EVENT = EventFactory.createArrayBacked(AccessoryAutoSyncEvent.class,
        listeners -> (stack, returnValue) -> {
            for (AccessoryAutoSyncEvent listener : listeners) {
                returnValue = listener.process(stack, returnValue);
            }

            return returnValue;
        }
    );

    boolean process(ItemStack stack, boolean original);
}
