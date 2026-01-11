package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 */
public abstract sealed class AccessoryTickEvent extends Event {
    public final Player player;
    public final ItemStack stack;

    public AccessoryTickEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public static final class Post extends AccessoryTickEvent {
        public Post(Player player, ItemStack stack) {
            super(player, stack);
        }
    }

    @Cancelable
    public static final class Pre extends AccessoryTickEvent {
        public Pre(Player player, ItemStack stack) {
            super(player, stack);
        }
    }
}
