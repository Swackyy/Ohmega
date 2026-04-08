package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryAllowWalkOnPowderSnowEvent {
    Event<AccessoryAllowWalkOnPowderSnowEvent> EVENT = EventFactory.createArrayBacked(AccessoryAllowWalkOnPowderSnowEvent.class,
        listeners -> (stack, ret) -> {
            for (AccessoryAllowWalkOnPowderSnowEvent listener : listeners) {
                ret = listener.process(stack, ret);
            }

            return ret;
        }
    );

    boolean process(ItemStack stack, boolean original);
}
