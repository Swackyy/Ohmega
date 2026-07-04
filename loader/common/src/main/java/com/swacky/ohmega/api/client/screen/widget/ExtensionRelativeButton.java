package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.LazyPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * A button that with position relative to the accessory extension with a top-left origin
 */
public abstract class ExtensionRelativeButton extends ExtensionScreenButton {
    /**
     * Constructs a new button that will appear visible only when the accessory extension attached to the screen is also visible,
     * and will also keep its position relative to the location of the extension
     * @param screen parent screen
     * @param position desired position for the button, will auto-adjust
     * @param width width of the button
     * @param height height of the button
     * @param textureLocation {@link Identifier} location of the texture to render for the button
     * @param component a {@link Component} that will be used for narration and possibly some other stuff
     */
    public ExtensionRelativeButton(@NonNull AbstractContainerScreen<?> screen, LazyPosition position, int width, int height, @NonNull Identifier textureLocation, @NonNull Component component) {
        super(screen, position, width, height, textureLocation, component);
    }

    @Override
    public int getAdjustedX() {
        return super.getAdjustedX() + accessoryScreen.getAccessoryExtensionPosition().x().get();
    }

    @Override
    public int getAdjustedY() {
        return super.getAdjustedY() + accessoryScreen.getAccessoryExtensionPosition().y().get();
    }
}
