package com.swacky.ohmega.client.screen.button;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public final class VisibilityButton extends AbstractButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/visibility_button.png");

    private final Player player;
    private final int index;

    public VisibilityButton(Player player, int index, int x, int y) {
        super(x, y, 6, 6, Component.empty());
        this.player = player;
        this.index = index;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        AccessoryHelper.getContainer(player).toggleHidden(player, index);
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        int hoveredOffsY;

        if (AccessoryHelper.getContainer(player).isHidden(index)) {
            hoveredOffsY = height;
        } else {
            hoveredOffsY = 0;
        }

        gui.blit(RenderPipelines.GUI_TEXTURED, LOCATION, getX(), getY(), 0, hoveredOffsY, getWidth(), getHeight(), 6, 12);

    }

    // todo: change later
    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
