package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;

import java.util.Map;

public interface AccessoryOverrideTypesEvent {
    Event<AccessoryOverrideTypesEvent> EVENT = EventFactory.createArrayBacked(AccessoryOverrideTypesEvent.class,
        listeners -> (overrideRemaps) -> {
            ImmutableMap.Builder<Item, AccessoryType> builder = new ImmutableMap.Builder<>();

            for (AccessoryOverrideTypesEvent listener : listeners) {
                builder.putAll(listener.process(ImmutableMap.copyOf(overrideRemaps)));
            }

            return builder.build();
        }
    );

    Map<Item, AccessoryType> process(ImmutableMap<Item, AccessoryType> view);
}
