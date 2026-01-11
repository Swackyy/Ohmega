package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashSet;
import java.util.Set;

public class RegisterAccessoryTypesEvent extends Event {
    public final Set<AccessoryType> types = new HashSet<>();
}
