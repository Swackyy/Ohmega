package com.swacky.ohmega.api.events;

import com.swacky.ohmega.api.IAccessory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired after {@code IAccessory.addDefaultAttributeModifiers()}
 * Using {@code getModifiers().clear()} will ensure no attribute modifiers are applied
 */
public class AccessoryAttributeModifiersEvent extends Event {
    private final Item item;
    private final IAccessory.ModifierBuilder modifiers;

    public AccessoryAttributeModifiersEvent(Item item, IAccessory.ModifierBuilder modifiers) {
        this.item = item;
        this.modifiers = modifiers;
    }

    public Item getItem() {
        return item;
    }

    public IAccessory.ModifierBuilder getModifiers() {
        return this.modifiers;
    }
}
