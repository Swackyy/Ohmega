package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;

import java.util.Map;

public final class RegisterAccessoryTypesEvent extends Event {
    private final Map<Identifier, AccessoryType> map;

    public RegisterAccessoryTypesEvent(Map<Identifier, AccessoryType> map) {
        this.map = map;
    }

    public void add(AccessoryType type) {
        map.put(type.getId(), type);
    }

    public void add(AccessoryType.Builder builder, Identifier id) {
        map.put(id, builder.build(id));
    }
}
