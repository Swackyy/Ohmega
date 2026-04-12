package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.IAccessory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onUnequip(LivingEntity, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanUnequipEvent}
 */
public interface AccessoryUnequipEvent {
    Event<AccessoryUnequipEvent> EVENT = EventFactory.createArrayBacked(AccessoryUnequipEvent.class,
        listeners -> (entity, stack) -> {
            for (AccessoryUnequipEvent listener : listeners) {
                if (listener.process(entity, stack)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(LivingEntity entity, ItemStack stack);
}
