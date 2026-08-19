package com.swacky.ohmega.api.client.event;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryRenderEvent extends Event {
    public final AccessoryRenderContext<?, ?> context;

    public AccessoryRenderEvent(AccessoryRenderContext<?, ?> context) {
        this.context = context;
    }

    public static final class Post extends AccessoryRenderEvent {
        public Post(AccessoryRenderContext<?, ?> context) {
            super(context);
        }
    }

    public static final class Pre extends AccessoryRenderEvent implements ICancellableEvent {
        public Pre(AccessoryRenderContext<?, ?> context) {
            super(context);
        }
    }
}
