package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jspecify.annotations.NonNull;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public final class AccessoryToggleExtensionEvent {
    public interface Post {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                listeners -> (accessoryMenu, value) -> {
                    for (Post listener : listeners) {
                        listener.process(accessoryMenu, value);
                    }
                }
        );

        void process(@NonNull IAccessoryMenu accessoryMenu, boolean value);
    }

    public interface Pre {
        Event<Pre> EVENT = EventFactory.createArrayBacked(Pre.class,
            listeners -> (accessoryMenu, value) -> {
                for (Pre listener : listeners) {
                    if (listener.process(accessoryMenu, value)) {
                        return true;
                    }
                }

                return false;
            }
        );

        boolean process(@NonNull IAccessoryMenu accessoryMenu, boolean value);
    }
}
