package com.swacky.ohmega.api.client.event;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public final class AccessoryExtensionRenderEvent {
    public interface Post {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                listeners -> (gui, extension) -> {
                    for (Post listener : listeners) {
                        listener.process(gui, extension);
                    }
                }
        );

        void process(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension);
    }

    public interface Pre {
        Event<Pre> EVENT = EventFactory.createArrayBacked(Pre.class,
            listeners -> (gui, extension) -> {
                for (Pre listener : listeners) {
                    if (listener.process(gui, extension)) {
                        return true;
                    }
                }

                return false;
            }
        );

        boolean process(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension);
    }
}
