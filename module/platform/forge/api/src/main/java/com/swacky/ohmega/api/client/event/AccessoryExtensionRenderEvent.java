package com.swacky.ohmega.api.client.event;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NonNull;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public sealed interface AccessoryExtensionRenderEvent {
    record Post(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) implements AccessoryExtensionRenderEvent, RecordEvent {
        public static final @NonNull EventBus<@NonNull Post> BUS = EventBus.create(Post.class);
    }

    record Pre(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) implements AccessoryExtensionRenderEvent, RecordEvent, Cancellable {
        public static final @NonNull CancellableEventBus<@NonNull Pre> BUS = CancellableEventBus.create(Pre.class);
    }
}
