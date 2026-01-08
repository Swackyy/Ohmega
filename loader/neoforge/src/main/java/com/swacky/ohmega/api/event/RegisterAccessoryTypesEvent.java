package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.neoforged.bus.api.Event;

import java.util.HashSet;
import java.util.Set;

public class RegisterAccessoryTypesEvent extends Event {
    private final Set<AccessoryType> types = new HashSet<>();

    public void addTypes(AccessoryType... types) {
        this.types.addAll(Set.of(types));
    }

    public Set<AccessoryType> getTypes() {
        return types;
    }
}
