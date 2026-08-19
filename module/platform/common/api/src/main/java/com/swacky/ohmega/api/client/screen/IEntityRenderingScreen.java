package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.client.screen.widget.IEditUiElement;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Implement this on your {@link IAccessoryScreen} if you want to be able to render a flippable entity in it
 * @apiNote This will only be relevant when the active extension implements {@link IEntityRenderingExtension}
 */
public interface IEntityRenderingScreen {
    /**
     * The (x, y) position the flip entity button will be added
     * @return the position to add the flip entity button
     * @apiNote Relative to {@link AbstractContainerScreen#leftPos} and {@link AbstractContainerScreen#topPos}
     */
    @NonNull LazyPosition getFlipEntityButtonPosition();

    /**
     * Get the lines to snap to when using magnetics.
     * {@link SnapLine}s are not relative to the current screen,
     * so you should use {@link AbstractContainerScreen#leftPos} and {@link AbstractContainerScreen#topPos}
     * @param screen parent screen
     * @param extension the screen's {@link AccessoryScreenExtension} instance
     * @return the list of {@link SnapLine}s that should be snapped to for use in magnetics,
     * or {@code null} to use the default {@link SnapLine}s ({@link IEditUiElement#getSnapLines(AbstractContainerScreen, AccessoryScreenExtension)}
     */
    default @Nullable List<SnapLine> getSnapLines(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryScreenExtension extension) {
        return null;
    }
}
