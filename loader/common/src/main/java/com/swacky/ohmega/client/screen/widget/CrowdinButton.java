package com.swacky.ohmega.client.screen.widget;

import com.swacky.ohmega.api.client.screen.widget.HoverableButton;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

public final class CrowdinButton extends HoverableButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/crowdin_button.png");
    public static final String TRANSLATION_KEY = Ohmega.MODID + ".widget.crowdin";

    private final Screen parentScreen;

    public CrowdinButton(Screen parentScreen) {
        super(6, parentScreen.height - 26, 20, 20, LOCATION, Component.translatable(TRANSLATION_KEY));

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
    protected boolean shouldOffsetY() {
        return isHovered;
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        setY(parentScreen.height - 26);
        super.extractContents(gui, mx, my, partialTicks);
    }
}
