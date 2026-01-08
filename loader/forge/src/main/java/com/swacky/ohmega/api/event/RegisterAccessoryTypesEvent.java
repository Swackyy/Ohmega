package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public class RegisterAccessoryTypesEvent extends MutableEvent {
    public static final EventBus<@NonNull RegisterAccessoryTypesEvent> BUS = EventBus.create(RegisterAccessoryTypesEvent.class);

    private final Set<AccessoryType> types = new HashSet<>();

    public void addTypes(AccessoryType... types) {
        this.types.addAll(Set.of(types));
    }

    public Set<AccessoryType> getTypes() {
        return types;
    }
}
