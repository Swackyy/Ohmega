package com.swacky.ohmega.api.client.event;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jspecify.annotations.NonNull;

public final class AccessoryExtensionInitEvent {
    public interface Post {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                listeners -> (extension, adder) -> {
                    for (Post listener : listeners) {
                        listener.process(extension, adder);
                    }
                }
        );

        void process(@NonNull AccessoryScreenExtension extension, AccessoryScreenExtension.@NonNull WidgetAdder adder);
    }

    public interface Pre {
        Event<Pre> EVENT = EventFactory.createArrayBacked(Pre.class,
            listeners -> (extension, adder) -> {
                for (Pre listener : listeners) {
                    if (listener.process(extension, adder)) {
                        return true;
                    }
                }

                return false;
            }
        );

        boolean process(@NonNull AccessoryScreenExtension extension, AccessoryScreenExtension.@NonNull WidgetAdder adder);
    }
}
