package com.swacky.ohmega.client.screen.widget;

import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.client.screen.widget.ExtensionRelativeButton;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public final class VisibilityButton extends ExtensionRelativeButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/visibility_button.png");

    private final Player player;
    private final int index;

    public VisibilityButton(AbstractContainerScreen<?> screen, int x, int y, Player player, int index) {
        super(screen, x, y, 6, 6, LOCATION);

        this.player = player;
        this.index = index;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        AccessoryHelper.getData(player).toggleHidden(player, index);
    }

    @Override
    protected boolean shouldOffsetY() {
        return AccessoryHelper.getData(player).isHidden(index);
    }
}
