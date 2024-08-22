package com.swacky.ohmega.api.events;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.IAccessory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class BindAccessoriesEvent extends Event implements IModBusEvent {
    private final Map<Item, IAccessory> additionsMap = new WeakHashMap<>();

    public boolean add(Item item, IAccessory binding) {
        if(item instanceof IAccessory || this.additionsMap.containsKey(item)) {
            return false;
        }
        this.additionsMap.put(item, binding);
        return true;
    }

    public ImmutableMap<Item, IAccessory> collect() {
        return new ImmutableMap.Builder<Item, IAccessory>().putAll(this.additionsMap).build();
    }
}
