package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanEquipEvent}
 */
public interface AccessoryEquipEvent {
    Event<AccessoryEquipEvent> EVENT = EventFactory.createArrayBacked(AccessoryEquipEvent.class,
        listeners -> (entity, stack, context) -> {
            for (AccessoryEquipEvent listener : listeners) {
                if (listener.process(entity, stack, context)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(LivingEntity entity, ItemStack stack, EquipContext context);
}
