package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public class RegisterAccessoryTypesEvent extends MutableEvent {
    public static final EventBus<@NonNull RegisterAccessoryTypesEvent> BUS = EventBus.create(RegisterAccessoryTypesEvent.class);

    public final Set<AccessoryType> types = new HashSet<>();
}
