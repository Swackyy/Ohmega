package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.HashSet;
import java.util.Set;

public interface RegisterAccessoryTypesEvent {
    Event<RegisterAccessoryTypesEvent> EVENT = EventFactory.createArrayBacked(RegisterAccessoryTypesEvent.class,
            listeners -> () -> {
                Set<AccessoryType> types = new HashSet<>();

                for (RegisterAccessoryTypesEvent listener : listeners) {
                    types.addAll(listener.process());
                }

                return types;
            }
    );

    Set<AccessoryType> process();
}
