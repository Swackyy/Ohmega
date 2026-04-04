package com.swacky.ohmega.client.screen.button;

import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

public final class CrowdinButton extends AbstractButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/crowdin_button.png");

    private final Screen parentScreen;

    public CrowdinButton(Screen parentScreen) {
        super(6, parentScreen.height - 26, 20, 20, Component.empty());
        this.parentScreen = parentScreen;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        Minecraft mc = parentScreen.minecraft;

        mc.setScreen(new ConfirmLinkScreen(result -> {
            if (result) {
                Util.getPlatform().openUri(OhmegaClient.LINK_CROWDIN);
            }

            mc.setScreen(parentScreen);
        }, OhmegaClient.LINK_CROWDIN, false));
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        int hoveredOffsY;

        if (isHovered()) {
            hoveredOffsY = height;
        } else {
            hoveredOffsY = 0;
        }

        setY(parentScreen.height - 26);
        gui.blit(RenderPipelines.GUI_TEXTURED, LOCATION, getX(), getY(), 0, hoveredOffsY, getWidth(), getHeight(), 20, 40);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
