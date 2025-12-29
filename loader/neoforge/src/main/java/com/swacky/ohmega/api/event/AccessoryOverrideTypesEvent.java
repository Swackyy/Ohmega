package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.WeakHashMap;

public final class AccessoryOverrideTypesEvent extends Event implements IModBusEvent {
    private final WeakHashMap<Item, AccessoryType> overrideRemaps = new WeakHashMap<>();

    public AccessoryOverrideTypesEvent put(Item item, AccessoryType type) {
        this.overrideRemaps.put(item, type);
        return this;
    }

    public ImmutableMap<Item, AccessoryType> get() {
        return ImmutableMap.copyOf(this.overrideRemaps);
    }
}
