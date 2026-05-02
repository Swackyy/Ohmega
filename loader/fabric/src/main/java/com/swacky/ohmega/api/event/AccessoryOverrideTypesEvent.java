package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.tuple.Pair;

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
    void process(Map<Item, Pair<AccessoryType, Boolean>> map);
}
