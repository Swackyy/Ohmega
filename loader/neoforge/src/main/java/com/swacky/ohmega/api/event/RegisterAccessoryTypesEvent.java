package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.neoforged.bus.api.Event;

import java.util.HashSet;
import java.util.Set;

public final class RegisterAccessoryTypesEvent extends Event {
    public final Set<AccessoryType> types = new HashSet<>();
}
