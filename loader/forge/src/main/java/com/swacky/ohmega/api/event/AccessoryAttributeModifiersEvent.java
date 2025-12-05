package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.ModifierHolder;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after {@link com.swacky.ohmega.api.IAccessory#addDefaultAttributeModifiers(ModifierHolder.Builder)}
 * Using {@link ModifierHolder.Builder#clear()} will ensure no attribute modifiers are applied
 */
public final class AccessoryAttributeModifiersEvent extends MutableEvent {
    public static final EventBus<@NotNull AccessoryAttributeModifiersEvent> BUS = EventBus.create(AccessoryAttributeModifiersEvent.class);

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
