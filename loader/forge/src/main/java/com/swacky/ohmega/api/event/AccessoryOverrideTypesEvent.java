package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.commons.lang3.tuple.Pair;

public final class AccessoryOverrideTypesEvent implements IModBusEvent {
    public final ImmutableMap.Builder<Item, Pair<AccessoryType, Boolean>> builder = new ImmutableMap.Builder<>();

    // If 'hard' is true, it will always override the type.
    // If 'hard' is false, it will only override the type if it does not already have one
    public void add(Item item, AccessoryType type, boolean hard) {
        builder.put(item, Pair.of(type, hard));
    }

    public ImmutableMap<Item, Pair<AccessoryType, Boolean>> getOverrides() {
        return builder.build();
    }
}
