package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Posted before the tick
 */
public final class AccessoryTickEvent {
    public interface Pre {
        Event<Pre> EVENT = EventFactory.createArrayBacked(Pre.class,
                listeners -> (player, stack) -> {
                    for (Pre listener : listeners) {
                        boolean result = listener.process(player, stack);

                        if (result) {
                            return true;
                        }
                    }

                    return false;
                }
        );

        boolean process(Player player, ItemStack stack);
    }

    public interface Post {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                listeners -> (player, stack) -> {
                    for (Post listener : listeners) {
                        listener.process(player, stack);
                    }
                }
        );

        void process(Player player, ItemStack stack);
    }
}
