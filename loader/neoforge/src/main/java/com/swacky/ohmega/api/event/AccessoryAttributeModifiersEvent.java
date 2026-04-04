package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addAttributeModifiers(AccessoryModifiers.Builder)}
 * Cancelling and using {@link AccessoryModifiers.Builder#clear()} will ensure no attribute modifiers are applied
 */
public final class AccessoryAttributeModifiersEvent extends Event implements ICancellableEvent {
    public final ItemStack stack;
    public final AccessoryModifiers.Builder builder;

    public AccessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        this.stack = stack;
        this.builder = builder;
    }
}
