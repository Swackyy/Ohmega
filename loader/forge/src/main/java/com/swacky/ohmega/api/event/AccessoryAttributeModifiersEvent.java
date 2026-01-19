package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NonNull;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(AccessoryModifiers.Builder)}
 * Cancelling and using {@link AccessoryModifiers.Builder#clear()} will ensure no attribute modifiers are applied
 */
public record AccessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) implements RecordEvent, Cancellable {
    public static final CancellableEventBus<@NonNull AccessoryAttributeModifiersEvent> BUS = CancellableEventBus.create(AccessoryAttributeModifiersEvent.class);
}
