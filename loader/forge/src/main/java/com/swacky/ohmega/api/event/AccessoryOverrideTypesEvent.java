package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.WeakHashMap;

public class AccessoryOverrideTypesEvent extends Event implements IModBusEvent {
    private final WeakHashMap<Item, AccessoryType> overrideRemaps = new WeakHashMap<>();

    public AccessoryOverrideTypesEvent put(Item item, AccessoryType type) {
        this.overrideRemaps.put(item, type);
        return this;
    }

    public ImmutableMap<Item, AccessoryType> get() {
        return ImmutableMap.copyOf(this.overrideRemaps);
    }
}
