package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class AccessoryUseEvent extends Event {
    public final Player player;
    public final ItemStack stack;

    public AccessoryUseEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }
}
