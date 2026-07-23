package com.swacky.ohmega.client.screen.widget;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IEntityRenderingScreen;
import com.swacky.ohmega.api.client.screen.SnapLine;
import com.swacky.ohmega.api.client.screen.widget.ExtensionScreenButton;
import com.swacky.ohmega.api.client.screen.widget.IEditUiElement;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class FlipEntityButton extends ExtensionScreenButton implements IEditUiElement {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/flip_entity_button.png");
    public static final String TRANSLATION_KEY = OhmegaClient.widgetTranslationKey("flip_entity");

    private final LazyPosition position;
    private final IEntityRenderingExtension extension;

    public FlipEntityButton(AbstractContainerScreen<?> screen, LazyPosition position, IEntityRenderingExtension extension) {
        super(screen, position, 9, 9, LOCATION, Component.translatable(TRANSLATION_KEY));

        this.extension = extension;
        this.position = position;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        extension.toggleFlipEntity();
    }

    @Override
    protected boolean shouldOffsetY() {
        return isHovered || extension.isEntityFlipped();
    }

    @Override
    public @NonNull LazyPosition getElementPosition() {
        return position;
    }

    @Override
    public @NonNull List<SnapLine> getSnapLines(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryScreenExtension extension) {
        List<SnapLine> lines = ((IEntityRenderingScreen) screen).getSnapLines(screen, extension);

        if (lines != null) {
            return lines;
        }

        return IEditUiElement.super.getSnapLines(screen, extension);
    }
}
