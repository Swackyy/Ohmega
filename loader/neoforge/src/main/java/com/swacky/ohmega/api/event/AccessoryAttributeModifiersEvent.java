package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(AccessoryModifiers.Builder)}
 * Using {@link AccessoryModifiers.Builder#clear()} will ensure no attribute modifiers are applied
 */
public final class AccessoryAttributeModifiersEvent extends Event {
    private final Item item;
    private final AccessoryModifiers.Builder builder;

    public AccessoryAttributeModifiersEvent(Item item, AccessoryModifiers.Builder builder) {
        this.item = item;
        this.builder = builder;
    }

    public Item getItem() {
        return item;
    }

    public AccessoryModifiers.Builder getBuilder() {
        return this.builder;
    }
}
