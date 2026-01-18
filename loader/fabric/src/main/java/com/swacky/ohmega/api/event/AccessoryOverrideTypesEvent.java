package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;

import java.util.Map;

public interface AccessoryOverrideTypesEvent {
    Event<AccessoryOverrideTypesEvent> EVENT = EventFactory.createArrayBacked(AccessoryOverrideTypesEvent.class,
        listeners -> (overrideRemaps) -> {
            for (AccessoryOverrideTypesEvent listener : listeners) {
                listener.process(overrideRemaps);
            }
        }
    );

    void process(Map<Item, AccessoryType> view);
}
