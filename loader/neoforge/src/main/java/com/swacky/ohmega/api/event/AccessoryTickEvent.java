package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryTickEvent extends Event implements ICancellableEvent permits AccessoryTickEvent.Pre, AccessoryTickEvent.Post {
    public final Player player;
    public final ItemStack stack;

    public AccessoryTickEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public static final class Pre extends AccessoryTickEvent {
        public Pre(Player player, ItemStack stack) {
            super(player, stack);
        }
    }

    public static final class Post extends AccessoryTickEvent {
        public Post(Player player, ItemStack stack) {
            super(player, stack);
        }
    }
}
