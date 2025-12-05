package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryTickEvent extends MutableEvent implements InheritableEvent permits AccessoryTickEvent.Pre, AccessoryTickEvent.Post {
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

    public static final class Pre extends AccessoryTickEvent implements Cancellable {
        public static final CancellableEventBus<@NotNull Pre> BUS = CancellableEventBus.create(Pre.class);

        public Pre(Player player, ItemStack stack) {
            super(player, stack);
        }
    }

    public static final class Post extends AccessoryTickEvent {
        public static final EventBus<@NotNull Post> BUS = EventBus.create(Post.class);

        public Post(Player player, ItemStack stack) {
            super(player, stack);
        }
    }
}
