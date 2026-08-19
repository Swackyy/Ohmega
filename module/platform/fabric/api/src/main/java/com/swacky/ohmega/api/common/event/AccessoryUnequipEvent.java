package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onUnequip(LivingEntity, ItemStack, EquipContext)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanUnequipEvent}
 */
public interface AccessoryUnequipEvent {
    Event<AccessoryUnequipEvent> EVENT = EventFactory.createArrayBacked(AccessoryUnequipEvent.class,
        listeners -> (entity, stack, context) -> {
            for (AccessoryUnequipEvent listener : listeners) {
                if (listener.process(entity, stack, context)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(LivingEntity entity, ItemStack stack, EquipContext context);
}
