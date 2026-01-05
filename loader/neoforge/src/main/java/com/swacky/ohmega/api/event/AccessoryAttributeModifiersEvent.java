package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(AccessoryModifiers.Builder)}
 * Using {@link AccessoryModifiers.Builder#clear()} will ensure no attribute modifiers are applied
 */
public final class AccessoryAttributeModifiersEvent extends Event {
    private final ItemStack stack;
    private final AccessoryModifiers.Builder builder;

    public AccessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        this.stack = stack;
        this.builder = builder;
    }

    public ItemStack getStack() {
        return stack;
    }

    public AccessoryModifiers.Builder getBuilder() {
        return this.builder;
    }
}
