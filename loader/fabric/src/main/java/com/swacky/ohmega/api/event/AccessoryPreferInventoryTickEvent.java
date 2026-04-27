package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryPreferInventoryTickEvent {
    Event<AccessoryPreferInventoryTickEvent> EVENT = EventFactory.createArrayBacked(AccessoryPreferInventoryTickEvent.class,
        listeners -> (stack, ret) -> {
            for (AccessoryPreferInventoryTickEvent listener : listeners) {
                ret = listener.process(stack, ret);
            }

            return ret;
        }
    );

    boolean process(ItemStack stack, boolean original);
}
