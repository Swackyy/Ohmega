package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.Identifier;

public interface RegisterAccessoryTypesEvent {
    Event<RegisterAccessoryTypesEvent> EVENT = EventFactory.createArrayBacked(RegisterAccessoryTypesEvent.class,
        listeners -> builder -> {
            for (RegisterAccessoryTypesEvent listener : listeners) {
                listener.process(builder);
            }
        }
    );

    void process(ImmutableMap.Builder<Identifier, AccessoryType> builder);
}
