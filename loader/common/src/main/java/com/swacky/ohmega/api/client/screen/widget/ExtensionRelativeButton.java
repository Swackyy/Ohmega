package com.swacky.ohmega.api.client.screen.widget;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;

/**
 * A button that with position relative to the accessory extension with the origin being the top-left
 */
public abstract class ExtensionRelativeButton extends ExtensionScreenButton {
    public ExtensionRelativeButton(AbstractContainerScreen<?> screen, int x, int y, int width, int height, Identifier textureLocation) {
        super(screen, x, y, width, height, textureLocation);
    }

    public int getAdjustedX() {
        return super.getAdjustedX() + accessoryScreen.getAccessoryExtensionX().get();
    }

    public int getAdjustedY() {
        return super.getAdjustedY() + accessoryScreen.getAccessoryExtensionY().get();
    }
}
