package com.swacky.ohmega.api.client.event;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryExtensionInitEvent extends Event {
    public final AccessoryScreenExtension extension;
    public final AccessoryScreenExtension.WidgetAdder adder;

    public AccessoryExtensionInitEvent(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder) {
        this.extension = extension;
        this.adder = adder;
    }

    public static final class Post extends AccessoryExtensionInitEvent {
        public Post(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder) {
            super(extension, adder);
        }
    }

    public static final class Pre extends AccessoryExtensionInitEvent implements ICancellableEvent {
        public Pre(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder) {
            super(extension, adder);
        }
    }
}
