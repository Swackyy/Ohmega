package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCompatibleWithEvent {
    Event<AccessoryCompatibleWithEvent> EVENT = EventFactory.createArrayBacked(AccessoryCompatibleWithEvent.class,
        listeners -> (stack, other, ret) -> {
            for (AccessoryCompatibleWithEvent listener : listeners) {
                ret = listener.process(stack, other, ret);
            }

            return ret;
        }
    );

    boolean process(ItemStack stack, ItemStack other, boolean original);
}
