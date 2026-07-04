package com.swacky.ohmega.api.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * A button that will change texture by offsetting by the provided height when {@link #shouldOffsetY()} returns {@code true}
 */
public abstract class HoverableButton extends AbstractButton {
    private final @NonNull Identifier textureLocation;

    /**
     * Constructs a new button that will change texture when {@link #shouldOffsetY()} returns {@code true}
     * @param x initial x-coordinate of this button
     * @param y initial y-coordinate of this button
     * @param width width of the button
     * @param height height of the button
     * @param textureLocation {@link Identifier} location of the texture to render for the button
     * @param component a {@link Component} that will be used for narration and possibly some other stuff
     */
    public HoverableButton(int x, int y, int width, int height, @NonNull Identifier textureLocation, @NonNull Component component) {
        super(x, y, width, height, component);

        this.textureLocation = textureLocation;
    }

    /**
     * Determines whether the alternative texture should be used
     * @return {@code true} to use the alternative texture for this render, {@code false} otherwise
     */
    protected abstract boolean shouldOffsetY();

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        int hoveredOffsY;

        if (shouldOffsetY()) {
            hoveredOffsY = height;
        } else {
            hoveredOffsY = 0;
        }

        gui.blit(RenderPipelines.GUI_TEXTURED, textureLocation, getX(), getY(), 0, hoveredOffsY, width, height, width, height * 2);
    }

    @Override
    public final void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
