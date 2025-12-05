package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.WeakHashMap;

public final class AccessoryOverrideTypesEvent extends MutableEvent implements IModBusEvent {
    private final WeakHashMap<Item, AccessoryType> overrides = new WeakHashMap<>();

    public AccessoryOverrideTypesEvent put(Item item, AccessoryType type) {
        this.overrides.put(item, type);
        return this;
    }

    public ImmutableMap<Item, AccessoryType> get() {
        return ImmutableMap.copyOf(this.overrides);
    }
}
