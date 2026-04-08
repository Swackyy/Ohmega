package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.tuple.Pair;

public interface AccessoryOverrideTypesEvent {
    Event<AccessoryOverrideTypesEvent> EVENT = EventFactory.createArrayBacked(AccessoryOverrideTypesEvent.class,
        listeners -> builder -> {
            for (AccessoryOverrideTypesEvent listener : listeners) {
                listener.process(builder);
            }
        }
    );

    // The boolean part is to represent a state of hard/soft
    // If true, it will always override the type.
    // If false, it will only override the type if it does not already have one
    void process(ImmutableMap.Builder<Item, Pair<AccessoryType, Boolean>> builder);
}
