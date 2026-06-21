package com.swacky.ohmega.client.screen.widget;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.widget.ExtensionScreenButton;
import com.swacky.ohmega.api.client.screen.widget.IEditUiElement;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.client.screen.SnapLine;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class ToggleExtensionButton extends ExtensionScreenButton implements IEditUiElement {
    public static final String TRANSLATION_KEY = Ohmega.MODID + ".widget.toggle_extension";

    private final LazyPosition position;
    private final AccessoryScreenExtension extension;
    private final boolean highlightWhenHovered;

    public ToggleExtensionButton(AbstractContainerScreen<?> screen, AccessoryScreenExtension extension, OhmegaConfig.Client.Service.ButtonStyle style) {
        LazyPosition position = ((IAccessoryScreen) screen).getAccessoryExtensionToggleButtonPosition(style);

        super(screen, position, style.width, style.height, style.textureLocation, Component.translatable(TRANSLATION_KEY));

        this.position = position;
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

    @Override
    public @NonNull LazyPosition getElementPosition() {
        return position;
    }

    @Override
    public @NonNull List<SnapLine> getSnapLines(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryScreenExtension extension) {
        for (Renderable renderable : screen.renderables) {
            if (renderable instanceof ImageButton button && button.sprites == RecipeBookComponent.RECIPE_BUTTON_SPRITES) {
                int x = button.getX();
                int y = button.getY();

                return List.of(
                        new SnapLine(true, x),
                        new SnapLine(false, y),
                        new SnapLine(true, x + button.getWidth() - 1),
                        new SnapLine(false, y + button.getHeight() - 1));
            }
        }

        return List.of();
    }
}