package com.swacky.ohmega.api.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryPreferVanillaUseEvent {
    Event<AccessoryPreferVanillaUseEvent> EVENT = EventFactory.createArrayBacked(AccessoryPreferVanillaUseEvent.class,
        listeners -> (stack, returnValue) -> {
            for (AccessoryPreferVanillaUseEvent listener : listeners) {
                returnValue = listener.process(stack, returnValue);
            }

            return returnValue;
        }
    );

    boolean process(ItemStack stack, boolean original);
}
