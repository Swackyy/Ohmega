package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;

import java.util.Map;

public interface AccessoryOverrideTypesEvent {
    Event<AccessoryOverrideTypesEvent> EVENT = EventFactory.createArrayBacked(AccessoryOverrideTypesEvent.class,
        listeners -> map -> {
            for (AccessoryOverrideTypesEvent listener : listeners) {
                listener.process(map);
            }
        }
    );

    // The boolean part is to represent a state of hard/soft
    // If true, it will always override the type.
    // If false, it will only override the type if it does not already have one
    void process(Map<Item, BooleanObjectPair<AccessoryType>> map);
}
