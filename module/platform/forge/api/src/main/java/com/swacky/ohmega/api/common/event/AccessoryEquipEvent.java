package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NonNull;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanEquipEvent}
 */
public record AccessoryEquipEvent(LivingEntity entity, ItemStack stack, EquipContext context) implements RecordEvent, Cancellable {
    public static final CancellableEventBus<@NonNull AccessoryEquipEvent> BUS = CancellableEventBus.create(AccessoryEquipEvent.class);
}
