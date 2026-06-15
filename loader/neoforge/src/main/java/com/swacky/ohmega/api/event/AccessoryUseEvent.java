package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

public final class AccessoryUseEvent extends Event implements ICancellableEvent {
    public final Player player;
    public final ItemStack stack;
    private final MutableBoolean shouldSynchronise;

    public AccessoryUseEvent(Player player, ItemStack stack, MutableBoolean shouldSynchronise) {
        this.player = player;
        this.stack = stack;
        this.shouldSynchronise = shouldSynchronise;
    }

    public void setSynchronise() {
        shouldSynchronise.setTrue();
    }
}
