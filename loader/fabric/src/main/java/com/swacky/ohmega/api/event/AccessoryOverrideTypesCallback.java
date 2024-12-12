package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;

import java.util.Map;

public interface AccessoryOverrideTypesCallback {
    Event<AccessoryOverrideTypesCallback> EVENT = EventFactory.createArrayBacked(AccessoryOverrideTypesCallback.class,
        listeners -> (overrideRemaps) -> {
            ImmutableMap.Builder<Item, AccessoryType> builder = new ImmutableMap.Builder<>();
            for (AccessoryOverrideTypesCallback listener : listeners) {
                builder.putAll(listener.process(ImmutableMap.copyOf(overrideRemaps)));
            }
            return builder.build();
        }
    );

    Map<Item, AccessoryType> process(ImmutableMap<Item, AccessoryType> view);
}
