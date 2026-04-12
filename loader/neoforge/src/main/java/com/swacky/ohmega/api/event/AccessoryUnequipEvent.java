package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.IAccessory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onUnequip(LivingEntity, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanUnequipEvent}
 */
public final class AccessoryUnequipEvent extends Event implements ICancellableEvent {
    public final LivingEntity entity;
    public final ItemStack stack;

    public AccessoryUnequipEvent(LivingEntity entity, ItemStack stack) {
        this.entity = entity;
        this.stack = stack;
    }
}
