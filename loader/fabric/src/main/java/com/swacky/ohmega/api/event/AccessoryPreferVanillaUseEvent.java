package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryPreferVanillaUseEvent {
    Event<AccessoryPreferVanillaUseEvent> EVENT = EventFactory.createArrayBacked(AccessoryPreferVanillaUseEvent.class,
        listeners -> (stack, ret) -> {
            for (AccessoryPreferVanillaUseEvent listener : listeners) {
                ret = listener.process(stack, ret);
            }

            return ret;
        }
    );

    boolean process(ItemStack stack, boolean original);
}
