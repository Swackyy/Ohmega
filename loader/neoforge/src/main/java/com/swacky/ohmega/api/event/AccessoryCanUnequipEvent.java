package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryCanUnequipEvent extends Event {
    public final Player player;
    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryCanUnequipEvent(Player player, ItemStack stack, boolean original) {
        this.player = player;
        this.stack = stack;
        this.returnValue = original;
    }
}
