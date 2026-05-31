package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Map;

public final class AccessoryOverrideTypesEvent implements IModBusEvent {
    private final Map<Item, BooleanObjectPair<AccessoryType>> map;

    public AccessoryOverrideTypesEvent(Map<Item, BooleanObjectPair<AccessoryType>> map) {
        this.map = map;
    }

    // If 'hard' is true, it will always override the type.
    // If 'hard' is false, it will only override the type if it does not already have one
    public void add(Item item, AccessoryType type, boolean hard) {
        map.put(item, BooleanObjectPair.of(hard, type));
    }
}
