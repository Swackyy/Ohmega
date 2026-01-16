package com.swacky.ohmega.api.event;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public final class AccessoryOverrideTypesEvent extends MutableEvent implements IModBusEvent {
    public final Map<Item, AccessoryType> overrides = new HashMap<>();
}
