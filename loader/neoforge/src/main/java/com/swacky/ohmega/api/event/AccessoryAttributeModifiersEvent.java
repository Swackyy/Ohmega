package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.ModifierHolder;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(ModifierHolder.Builder)}
 * Using {@link ModifierHolder.Builder#clear()} will ensure no attribute modifiers are applied
 */
public class AccessoryAttributeModifiersEvent extends Event {
    private final Item item;
    private final ModifierHolder.Builder builder;

    public AccessoryAttributeModifiersEvent(Item item, ModifierHolder.Builder builder) {
        this.item = item;
        this.builder = builder;
    }

    public Item getItem() {
        return item;
    }

    public ModifierHolder.Builder getBuilder() {
        return this.builder;
    }
}
