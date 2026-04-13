package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryRenderItemEvent extends Event {
    public final AccessoryRenderContext context;

    public AccessoryRenderItemEvent(AccessoryRenderContext context) {
        this.context = context;
    }

    public static final class Pre extends AccessoryRenderItemEvent implements ICancellableEvent {
        public Pre(AccessoryRenderContext context) {
            super(context);
        }
    }

    public static final class Post extends AccessoryRenderItemEvent {
        public Post(AccessoryRenderContext context) {
            super(context);
        }
    }
}
