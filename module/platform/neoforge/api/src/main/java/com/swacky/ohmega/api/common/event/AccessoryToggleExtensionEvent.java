package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jspecify.annotations.NonNull;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryToggleExtensionEvent extends Event {
    public final @NonNull IAccessoryMenu accessoryMenu;
    public final boolean value;

    public AccessoryToggleExtensionEvent(@NonNull IAccessoryMenu accessoryMenu, boolean value) {
        this.accessoryMenu = accessoryMenu;
        this.value = value;
    }

    public static final class Post extends AccessoryToggleExtensionEvent {
        public Post(@NonNull IAccessoryMenu accessoryMenu, boolean value) {
            super(accessoryMenu, value);
        }
    }

    public static final class Pre extends AccessoryToggleExtensionEvent implements ICancellableEvent {
        public Pre(@NonNull IAccessoryMenu accessoryMenu, boolean value) {
            super(accessoryMenu, value);
        }
    }
}
