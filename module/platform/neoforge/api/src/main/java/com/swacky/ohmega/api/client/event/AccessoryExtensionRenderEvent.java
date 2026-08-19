package com.swacky.ohmega.api.client.event;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Cancelling only has effect when used in {@link Pre}, stopping the ticking of the item
 */
public abstract sealed class AccessoryExtensionRenderEvent extends Event {
    public final GuiGraphicsExtractor gui;
    public final AccessoryScreenExtension extension;

    public AccessoryExtensionRenderEvent(GuiGraphicsExtractor gui, AccessoryScreenExtension extension) {
        this.gui = gui;
        this.extension = extension;
    }

    public static final class Post extends AccessoryExtensionRenderEvent {
        public Post(GuiGraphicsExtractor gui, AccessoryScreenExtension extension) {
            super(gui, extension);
        }
    }

    public static final class Pre extends AccessoryExtensionRenderEvent implements ICancellableEvent {
        public Pre(GuiGraphicsExtractor gui, AccessoryScreenExtension extension) {
            super(gui, extension);
        }
    }
}
