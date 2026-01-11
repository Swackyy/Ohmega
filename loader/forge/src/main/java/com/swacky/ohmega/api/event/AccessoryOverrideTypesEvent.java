package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public final class AccessoryOverrideTypesEvent extends Event implements IModBusEvent {
    public final Map<Item, AccessoryType> overrides = new HashMap<>();
}
