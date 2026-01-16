package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public final class AccessoryUseEvent extends Event implements ICancellableEvent {
    public final Player player;
    public final ItemStack stack;

    public AccessoryUseEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }
}
