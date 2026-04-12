package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCanUnequipEvent {
    Event<AccessoryCanUnequipEvent> EVENT = EventFactory.createArrayBacked(AccessoryCanUnequipEvent.class,
        listeners -> (entity, stack, ret) -> {
            for (AccessoryCanUnequipEvent listener : listeners) {
                ret = listener.process(entity, stack, ret);
            }

            return ret;
        }
    );

    boolean process(LivingEntity entity, ItemStack stack, boolean original);
}
