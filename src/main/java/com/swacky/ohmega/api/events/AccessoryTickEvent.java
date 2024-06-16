package com.swacky.ohmega.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@code Phase.PRE}, stopping the ticking of the item
 */
@Cancelable
public class AccessoryTickEvent extends Event {
    public final Phase phase;
    private final Player player;
    private final ItemStack stack;

    public AccessoryTickEvent(Phase phase, Player player, ItemStack stack) {
        this.phase = phase;
        this.player = player;
        this.stack = stack;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
