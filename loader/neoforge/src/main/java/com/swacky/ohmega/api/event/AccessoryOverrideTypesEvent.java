package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class AccessoryOverrideTypesEvent extends Event implements IModBusEvent {
    private final Map<Item, Pair<AccessoryType, Boolean>> map;

    public AccessoryOverrideTypesEvent(Map<Item, Pair<AccessoryType, Boolean>> map) {
        this.map = map;
    }

    // If 'hard' is true, it will always override the type.
    // If 'hard' is false, it will only override the type if it does not already have one
    public void add(Item item, AccessoryType type, boolean hard) {
        map.put(item, Pair.of(type, hard));
    }
}
