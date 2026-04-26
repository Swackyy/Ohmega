package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtensions;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;

public abstract class ExtensionRelativeButton extends ExtensionContainerScreenButton {
    public ExtensionRelativeButton(AbstractContainerScreen<?> screen, int x, int y, int width, int height, Identifier textureLocation) {
        super(screen, x, y, width, height, textureLocation);
    }

    public int getAdjustedX() {
        return super.getAdjustedX() + AccessoryScreenExtensions.getAccessoryExtensionX(accessoryScreen);
    }

    public int getAdjustedY() {
        return super.getAdjustedY() + AccessoryScreenExtensions.getAccessoryExtensionY(accessoryScreen);
    }
}
