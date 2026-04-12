package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NonNull;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public sealed interface AccessoryTickEvent {
    record Pre(LivingEntity entity, ItemStack stack) implements AccessoryTickEvent, RecordEvent, Cancellable {
        public static final CancellableEventBus<@NonNull Pre> BUS = CancellableEventBus.create(Pre.class);
    }

    record Post(LivingEntity entity, ItemStack stack) implements AccessoryTickEvent, RecordEvent, Cancellable {
        public static final CancellableEventBus<@NonNull Post> BUS = CancellableEventBus.create(Post.class);
    }
}
