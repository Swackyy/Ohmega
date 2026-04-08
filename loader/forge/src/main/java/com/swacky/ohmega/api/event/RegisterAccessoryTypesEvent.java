package com.swacky.ohmega.api.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class RegisterAccessoryTypesEvent extends MutableEvent {
    public static final EventBus<@NonNull RegisterAccessoryTypesEvent> BUS = EventBus.create(RegisterAccessoryTypesEvent.class);

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
