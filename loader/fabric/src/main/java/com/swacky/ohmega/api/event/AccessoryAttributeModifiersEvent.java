package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(AccessoryModifiers.Builder)}
 * Cancelling and using {@link AccessoryModifiers.Builder#clear()} will ensure no attribute modifiers are applied
 */
public interface AccessoryAttributeModifiersEvent {
    Event<AccessoryAttributeModifiersEvent> EVENT = EventFactory.createArrayBacked(AccessoryAttributeModifiersEvent.class,
        listeners -> (stack, builder) -> {
            for (AccessoryAttributeModifiersEvent listener : listeners) {
                if (listener.process(stack, builder)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(ItemStack stack, AccessoryModifiers.Builder builder);
}
