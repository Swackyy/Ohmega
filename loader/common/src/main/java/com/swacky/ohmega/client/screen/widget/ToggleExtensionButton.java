package com.swacky.ohmega.client.screen.widget;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.widget.ExtensionContainerScreenButton;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NonNull;

public final class ToggleExtensionButton extends ExtensionContainerScreenButton {
    private final AccessoryScreenExtension extension;
    private final boolean highlightWhenHovered;

    public ToggleExtensionButton(AbstractContainerScreen<?> screen, AccessoryScreenExtension extension, OhmegaConfig.Client.Service.ButtonStyle style) {
        IntIntPair position = ((IAccessoryScreen) screen).getAccessoryExtensionToggleButtonPosition(style);

        super(screen, position.firstInt(), position.secondInt(), style.width, style.height, style.textureLocation);

        this.extension = extension;
        this.highlightWhenHovered = style.highlightWhenHovered;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            boolean value = !accessoryScreen.isAccessoryExtensionVisible();

            extension.setVisible(value);
        }
    }

    @Override
    protected boolean shouldOffsetY() {
        return accessoryScreen.isAccessoryExtensionVisible() || (highlightWhenHovered && isHoveredOrFocused());
    }
}
