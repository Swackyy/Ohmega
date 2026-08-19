package com.swacky.ohmega.api.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCompatibleWithEvent {
    Event<AccessoryCompatibleWithEvent> EVENT = EventFactory.createArrayBacked(AccessoryCompatibleWithEvent.class,
        listeners -> (stack, other, returnValue) -> {
            for (AccessoryCompatibleWithEvent listener : listeners) {
                returnValue = listener.process(stack, other, returnValue);
            }

            return returnValue;
        }
    );

    boolean process(ItemStack stack, ItemStack other, boolean original);
}
