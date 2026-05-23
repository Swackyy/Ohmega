package com.swacky.ohmega.api.client.screen.widget;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;

public abstract class ExtensionRelativeButton extends ExtensionContainerScreenButton {
    public ExtensionRelativeButton(AbstractContainerScreen<?> screen, int x, int y, int width, int height, Identifier textureLocation) {
        super(screen, x, y, width, height, textureLocation);
    }

    public int getAdjustedX() {
        return super.getAdjustedX() + accessoryScreen.getAccessoryExtensionX();
    }

    public int getAdjustedY() {
        return super.getAdjustedY() + accessoryScreen.getAccessoryExtensionY();
    }
}
