package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.ModifierHolder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(ModifierHolder.Builder)}
 * Using {@link ModifierHolder.Builder#clear()} will ensure no attribute modifiers are applied
 */
public interface AccessoryAttributeModifiersCallback {
    Event<AccessoryAttributeModifiersCallback> EVENT = EventFactory.createArrayBacked(AccessoryAttributeModifiersCallback.class,
        listeners -> (item, builder) -> {
            for (AccessoryAttributeModifiersCallback listener : listeners) {
                listener.process(item, builder);
            }
        }
    );

    void process(Item item, ModifierHolder.Builder builder);
}
