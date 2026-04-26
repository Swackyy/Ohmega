package com.swacky.ohmega.api.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public abstract class HoverableButton extends AbstractButton {
    private final Identifier textureLocation;

    public HoverableButton(int x, int y, int width, int height, Identifier textureLocation) {
        // todo: change Component.empty() to supplying one for narration
        super(x, y, width, height, Component.empty());

        this.textureLocation = textureLocation;
    }

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
