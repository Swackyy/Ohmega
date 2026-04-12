package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.EquipContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCanEquipEvent {
    Event<AccessoryCanEquipEvent> EVENT = EventFactory.createArrayBacked(AccessoryCanEquipEvent.class,
        listeners -> (entity, stack, context, ret) -> {
            for (AccessoryCanEquipEvent listener : listeners) {
                ret = listener.process(entity, stack, context, ret);
            }

            return ret;
        }
    );

    boolean process(LivingEntity entity, ItemStack stack, EquipContext context, boolean original);
}
