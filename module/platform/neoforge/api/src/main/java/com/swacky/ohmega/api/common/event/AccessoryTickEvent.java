package com.swacky.ohmega.api.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryTickEvent extends Event {
    public final LivingEntity entity;
    public final ItemStack stack;

    public AccessoryTickEvent(LivingEntity entity, ItemStack stack) {
        this.entity = entity;
        this.stack = stack;
    }

    public static final class Post extends AccessoryTickEvent {
        public Post(LivingEntity entity, ItemStack stack) {
            super(entity, stack);
        }
    }

    public static final class Pre extends AccessoryTickEvent implements ICancellableEvent {
        public Pre(LivingEntity entity, ItemStack stack) {
            super(entity, stack);
        }
    }
}
