package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class AccessoryRenderEvent {
    public interface Pre {
        Event<Pre> EVENT = EventFactory.createArrayBacked(Pre.class,
            listeners -> context -> {
                for (Pre listener : listeners) {
                    if (listener.process(context)) {
                        return true;
                    }
                }

                return false;
            }
        );

        boolean process(AccessoryRenderContext<?, ?> context);
    }

    public interface Post {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
            listeners -> context -> {
                for (Post listener : listeners) {
                    listener.process(context);
                }
            }
        );

        void process(AccessoryRenderContext<?, ?> context);
    }
}
