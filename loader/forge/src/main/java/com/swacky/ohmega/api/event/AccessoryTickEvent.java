package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
@Cancelable
public abstract class AccessoryTickEvent extends Event {
    private final Player player;
    private final ItemStack stack;

    public AccessoryTickEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public static class Pre extends AccessoryTickEvent {
        public Pre(Player player, ItemStack stack) {
            super(player, stack);
        }
    }

    public static class Post extends AccessoryTickEvent {
        public Post(Player player, ItemStack stack) {
            super(player, stack);
        }
    }
}
