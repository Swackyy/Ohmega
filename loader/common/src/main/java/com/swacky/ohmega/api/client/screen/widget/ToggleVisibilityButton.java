package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

/**
 * Button for toggling accessory visibility for items with an accessory renderer
 */
public final class ToggleVisibilityButton extends ExtensionRelativeButton {
    private static final Identifier LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/toggle_visibility_button.png");
    public static final String TRANSLATION_KEY = Ohmega.MODID + ".widget.toggle_visibility";

    private final Player player;
    private final AccessoryData data;
    private final int index;

    /**
     *
     * @param screen parent screen
     * @param x initial x-coordinate of this button
     * @param y initial y-coordinate of this button
     * @param player the {@link Player} owning the screen
     * @param index the slot or entry index for visibility toggling
     */
    public ToggleVisibilityButton(AbstractContainerScreen<?> screen, int x, int y, Player player, int index) {
        super(screen, new LazyPosition(IntLazySavedValue.of(x), IntLazySavedValue.of(y)), 6, 6, LOCATION, Component.translatable(TRANSLATION_KEY));

        this.player = player;
        this.data = OhmegaDataAttachments.getData(player);
        this.index = index;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        data.getEntry(index).toggleHidden(player, index);
    }

    @Override
    protected boolean shouldOffsetY() {
        return data.getEntry(index).isHidden();
    }
}
