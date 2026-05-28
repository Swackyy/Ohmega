package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryAutoSyncModuloEvent {
    Event<AccessoryAutoSyncModuloEvent> EVENT = EventFactory.createArrayBacked(AccessoryAutoSyncModuloEvent.class,
        listeners -> (stack, returnValue) -> {
            for (AccessoryAutoSyncModuloEvent listener : listeners) {
                returnValue = listener.process(stack, returnValue);
            }

            return returnValue;
        }
    );

    byte process(ItemStack stack, byte original);
}
