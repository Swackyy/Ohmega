package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.client.screen.widget.FlipEntityButton;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/**
 * Implement this on your {@link IAccessoryScreen} if you want to be able to render a flippable entity in it
 * <p>
 * This will only be relevant when the active extension implements {@link IEntityRenderingExtension}
 */
public interface IEntityRenderingScreen {
    /**
     * The (x, y) position the {@link FlipEntityButton} will be added
     * <p>
     * Relative to {@link AbstractContainerScreen#leftPos} and {@link AbstractContainerScreen#topPos}
     * @return the position to add the {@link FlipEntityButton}
     */
    IntIntPair getFlipEntityButtonPosition();
}
