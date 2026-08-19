package com.swacky.ohmega.api.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public final class AccessoryTickEvent {
    public interface Post {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                listeners -> (entity, stack) -> {
                    for (Post listener : listeners) {
                        listener.process(entity, stack);
                    }
                }
        );

        void process(LivingEntity entity, ItemStack stack);
    }

    public interface Pre {
        Event<Pre> EVENT = EventFactory.createArrayBacked(Pre.class,
            listeners -> (entity, stack) -> {
                for (Pre listener : listeners) {
                    if (listener.process(entity, stack)) {
                        return true;
                    }
                }

                return false;
            }
        );

        boolean process(LivingEntity entity, ItemStack stack);
    }
}
