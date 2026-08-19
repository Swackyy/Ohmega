package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.client.screen.SnapLine;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Should be implemented for elements that should be able to be moved or otherwise edited through the edit UI screen
 */
public interface IEditUiElement {
    /**
     * Get the position of this element relative to the top left of the parent screen
     * @return the element's position
     */
    @NonNull LazyPosition getElementPosition();

    /**
     * Get the width of this element
     * @return element width
     */
    int getWidth();

    /**
     * Get the width of this element
     * @return element height
     */
    int getHeight();

    /**
     * Check if the element is "active", usually meaning if it is visible.
     * This may be useful for elements that only display when the extension is active
     * @return {@code true} if the element should be considered for editing at the current time, {@code false} to ignore it
     */
    boolean isActive();

    /**
     * Determines whether the position of the element is relative to the accessory extension instead of the parent screen
     * @return {@code true} if the position is relative to the accessory extension, {@code false} if it is relative to the top left of the parent screen
     */
    default boolean isExtensionRelative() {
        return false;
    }

    /**
     * Get the {@link Rect2i} shapes that this element has, used mainly in highlighting
     * @return a list of the shapes this element occupies
     */
    default @NonNull List<Rect2i> getRects() {
        return List.of(new Rect2i(
                0,
                0,
                getWidth(),
                getHeight()));
    }

    /**
     * Get the lines to snap to when using magnetics.
     * {@link SnapLine}s are not relative to the current screen,
     * so you should use {@link AbstractContainerScreen#leftPos} and {@link AbstractContainerScreen#topPos}
     * @param screen parent screen
     * @param extension the screen's {@link AccessoryScreenExtension} instance
     * @return the list of {@link SnapLine}s that should be snapped to for use in magnetics
     */
    default @NonNull List<SnapLine> getSnapLines(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryScreenExtension extension) {
        if (isExtensionRelative()) {
            LazyPosition position = extension.getElementPosition();
            int x = screen.leftPos + position.x().get();
            int y = screen.topPos + position.y().get();

            return List.of(
                    new SnapLine(true, x),
                    new SnapLine(false, y),
                    new SnapLine(true, x + extension.getWidth() - 1),
                    new SnapLine(false, y + extension.getHeight() - 1));
        }

        return List.of(
                new SnapLine(true, screen.width / 2),
                new SnapLine(false, screen.height / 2),
                new SnapLine(true, screen.leftPos),
                new SnapLine(false, screen.topPos),
                new SnapLine(true, screen.leftPos + screen.imageWidth - 1),
                new SnapLine(false, screen.topPos + screen.imageHeight - 1));
    }

    // Right-click submenu options?
}
