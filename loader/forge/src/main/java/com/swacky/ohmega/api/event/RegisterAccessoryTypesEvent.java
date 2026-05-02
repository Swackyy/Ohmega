package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public final class RegisterAccessoryTypesEvent extends MutableEvent {
    public static final EventBus<@NonNull RegisterAccessoryTypesEvent> BUS = EventBus.create(RegisterAccessoryTypesEvent.class);

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
