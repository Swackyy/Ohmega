package com.swacky.ohmega.api.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryPreferInventoryTickEvent {
    Event<AccessoryPreferInventoryTickEvent> EVENT = EventFactory.createArrayBacked(AccessoryPreferInventoryTickEvent.class,
        listeners -> (stack, returnValue) -> {
            for (AccessoryPreferInventoryTickEvent listener : listeners) {
                returnValue = listener.process(stack, returnValue);
            }

            return returnValue;
        }
    );

    boolean process(ItemStack stack, boolean original);
}
