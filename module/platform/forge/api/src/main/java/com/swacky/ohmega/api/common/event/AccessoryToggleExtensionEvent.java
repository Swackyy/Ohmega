package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
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
public sealed interface AccessoryToggleExtensionEvent {
    record Post(@NonNull IAccessoryMenu accessoryMenu, boolean value) implements AccessoryToggleExtensionEvent, RecordEvent {
        public static final EventBus<@NonNull Post> BUS = EventBus.create(Post.class);
    }

    record Pre(@NonNull IAccessoryMenu accessoryMenu, boolean value) implements AccessoryToggleExtensionEvent, RecordEvent, Cancellable {
        public static final CancellableEventBus<@NonNull Pre> BUS = CancellableEventBus.create(Pre.class);
    }
}
