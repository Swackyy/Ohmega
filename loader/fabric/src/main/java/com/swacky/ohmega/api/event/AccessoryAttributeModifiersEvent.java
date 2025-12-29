package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(AccessoryModifiers.Builder)}
 * Using {@link AccessoryModifiers.Builder#clear()} will ensure no attribute modifiers are applied
 */
public interface AccessoryAttributeModifiersEvent {
    Event<AccessoryAttributeModifiersEvent> EVENT = EventFactory.createArrayBacked(AccessoryAttributeModifiersEvent.class,
        listeners -> (item, builder) -> {
            for (AccessoryAttributeModifiersEvent listener : listeners) {
                listener.process(item, builder);
            }
        }
    );

    void process(Item item, AccessoryModifiers.Builder builder);
}
