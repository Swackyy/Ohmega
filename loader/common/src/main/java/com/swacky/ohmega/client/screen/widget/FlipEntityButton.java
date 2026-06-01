package com.swacky.ohmega.client.screen.widget;

import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.widget.ExtensionScreenButton;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public final class FlipEntityButton extends ExtensionScreenButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/flip_entity_button.png");
    public static final String TRANSLATION_KEY = Ohmega.MODID + ".widget.flip_entity";

    private final IEntityRenderingExtension extension;

    public FlipEntityButton(AbstractContainerScreen<?> screen, IEntityRenderingExtension extension, int x, int y) {
        super(screen, x, y, 9, 9, LOCATION, Component.translatable(TRANSLATION_KEY));

        this.extension = extension;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        extension.toggleFlipEntity();
    }

    @Override
    protected boolean shouldOffsetY() {
        return isHovered || extension.isEntityFlipped();
    }
}
