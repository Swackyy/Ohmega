package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * A button that will only appear visible if the accessory extension is visible
 */
public abstract class ExtensionScreenButton extends HoverableButton {
    protected final AbstractContainerScreen<?> screen;
    protected final IAccessoryScreen accessoryScreen;
    private final int desiredX;
    private final int desiredY;

    public ExtensionScreenButton(AbstractContainerScreen<?> screen, int x, int y, int width, int height, Identifier textureLocation) {
        super(x, y, width, height, textureLocation);

        this.screen = screen;
        this.accessoryScreen = (IAccessoryScreen) screen;
        this.desiredX = x;
        this.desiredY = y;
    }

    public int getAdjustedX() {
        return desiredX + screen.leftPos;
    }

    public int getAdjustedY() {
        return desiredY + screen.topPos;
    }

    @Override
    public final void extractRenderState(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        visible = accessoryScreen.areAccessoryExtensionWidgetsVisible();

        super.extractRenderState(gui, mx, my, partialTicks);
    }

    @Override
    protected final void extractContents(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        setX(getAdjustedX());
        setY(getAdjustedY());
        super.extractContents(gui, mx, my, partialTicks);
    }
}
