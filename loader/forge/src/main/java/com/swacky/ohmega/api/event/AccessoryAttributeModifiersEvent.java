package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NonNull;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(AccessoryModifiers.Builder)}
 * Using {@link AccessoryModifiers.Builder#clear()} will ensure no attribute modifiers are applied
 */
public record AccessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) implements RecordEvent {
    public static final EventBus<@NonNull AccessoryAttributeModifiersEvent> BUS = EventBus.create(AccessoryAttributeModifiersEvent.class);
}
