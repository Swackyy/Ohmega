package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * A button that will only appear visible if the accessory extension is visible
 */
public abstract class ExtensionScreenButton extends HoverableButton {
    protected final @NonNull AbstractContainerScreen<?> screen;
    protected final @NonNull IAccessoryScreen accessoryScreen;
    private final LazyPosition position;

    /**
     * Constructs a new button that will appear visible only when the accessory extension attached to the screen is also visible
     * @param screen parent screen
     * @param position desired position for the button, will auto-adjust
     * @param width width of the button
     * @param height height of the button
     * @param textureLocation {@link Identifier} location of the texture to render for the button
     * @param component a {@link Component} that will be used for narration and possibly some other stuff
     */
    public ExtensionScreenButton(@NonNull AbstractContainerScreen<?> screen, LazyPosition position, int width, int height, @NonNull Identifier textureLocation, @NonNull Component component) {
        super(position.x().get(), position.y().get(), width, height, textureLocation, component);

        this.screen = screen;
        this.accessoryScreen = (IAccessoryScreen) screen;
        this.position = position;
    }

    /**
     * Gets the new {@code x} position based on the original position and the screen's position
     * @return the adjusted {@code x} coordinate for the next render
     * @apiNote This is called on every {@link #extractContents(GuiGraphicsExtractor, int, int, float)} invocation
     */
    public int getAdjustedX() {
        return position.x().get() + screen.leftPos;
    }

    /**
     * Gets the new {@code y} position based on the original position and the screen's position
     * @return the adjusted {@code y} coordinate for the next render
     * @apiNote This is called on every {@link #extractContents(GuiGraphicsExtractor, int, int, float)} invocation
     */
    public int getAdjustedY() {
        return position.y().get() + screen.topPos;
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
