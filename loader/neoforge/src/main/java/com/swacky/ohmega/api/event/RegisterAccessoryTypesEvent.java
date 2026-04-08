package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;

public final class RegisterAccessoryTypesEvent extends Event {
    private final ImmutableMap.Builder<Identifier, AccessoryType> builder = new ImmutableMap.Builder<>();

    public void add(AccessoryType type) {
        builder.put(type.getId(), type);
    }

    public void add(AccessoryType.Builder builder, Identifier id) {
        this.builder.put(id, builder.build(id));
    }

    public ImmutableMap<Identifier, AccessoryType> getTypes() {
        return builder.build();
    }
}
