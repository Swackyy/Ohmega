package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class VisibilityButton extends AbstractButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/visibility_button.png");

    public VisibilityButton(int x, int y) {
        super(x, y, 9, 9, Component.empty());
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {

    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        int hoveredOffsY;

        if (isHovered() /* || other check */) {
            hoveredOffsY = height;
        } else {
            hoveredOffsY = 0;
        }

        gui.blit(RenderPipelines.GUI_TEXTURED, LOCATION, getX(), getY(), 0, hoveredOffsY, getWidth(), getHeight(), 9, 18);

    }

    // todo: change later
    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
