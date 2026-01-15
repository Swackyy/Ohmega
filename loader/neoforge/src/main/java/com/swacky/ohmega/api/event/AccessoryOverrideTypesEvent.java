package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.WeakHashMap;

public final class AccessoryOverrideTypesEvent extends Event implements IModBusEvent {
    public final WeakHashMap<Item, AccessoryType> overrideRemaps = new WeakHashMap<>();
}
