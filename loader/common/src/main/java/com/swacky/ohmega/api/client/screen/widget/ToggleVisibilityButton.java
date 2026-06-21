package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public final class ToggleVisibilityButton extends ExtensionRelativeButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/toggle_visibility_button.png");
    public static final String TRANSLATION_KEY = Ohmega.MODID + ".widget.toggle_visibility";

    private final Player player;
    private final int index;

    public ToggleVisibilityButton(AbstractContainerScreen<?> screen, int x, int y, Player player, int index) {
        super(screen, new LazyPosition(IntLazySavedValue.of(x), IntLazySavedValue.of(y)), 6, 6, LOCATION, Component.translatable(TRANSLATION_KEY));

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
