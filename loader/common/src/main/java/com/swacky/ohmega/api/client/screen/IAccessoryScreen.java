package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.client.screen.EditUiScreen;
import com.swacky.ohmega.client.screen.widget.ToggleExtensionButton;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by {@link AbstractContainerScreen}s to allow them to have an accessory extension.
 * See {@link AccessoryScreens} for crucial implementation details
 * <p>
 * This also contains some utility methods which you <strong>should</strong> call to implement complete functionality
 * failing to call them may cause your screen to not have an extension applied
 * <p>
 * By default, this is applied by Ohmega via mixin to:
 * <ul>
 *     <li>{@link CreativeModeInventoryScreen}</li>
 *     <li>{@link InventoryScreen}</li>
 * </ul>
 */
public interface IAccessoryScreen {
    /**
     * Retrieve the accessory extension bound to this screen.
     * If using this, you should almost always check for nullability
     * @return bound accessory extension
     */
    @Nullable AccessoryScreenExtension getAccessoryExtension();

    /**
     * Set the accessory extension bound to this screen.
     * This is called in {@link AccessoryScreens#onConstruct(AbstractContainerScreen)} (which you should be calling) for you
     * @param extension accessory extension to set to
     */
    void setAccessoryExtension(@NonNull AccessoryScreenExtension extension);

    /**
     * The position, relative to {@link AbstractContainerScreen#leftPos}, where the extension will be placed.
     * This is implemented as a {@link LazyPosition} as to allow for changing this value via the {@link EditUiScreen}
     * @return relative position to place the extension
     */
    @NonNull LazyPosition getAccessoryExtensionPosition();

    /**
     * The (x, y) position the {@link ToggleExtensionButton} will be added.
     * You should use a {@code switch} statement to handle the case for each style (handling {@link OhmegaConfig.Client.Service.ButtonStyle#HIDDEN} is not needed,
     * and you should instead use a {@code case default} to finish)
     * <p>
     * Relative to {@link AbstractContainerScreen#leftPos} and {@link AbstractContainerScreen#topPos}
     * @param style the button style currently in use
     * @return the position to add the {@link ToggleExtensionButton}
     */
    @NonNull LazyPosition getAccessoryExtensionToggleButtonPosition(OhmegaConfig.Client.Service.ButtonStyle style);

    /**
     * A per-screen function that determines whether the extension should be shown.
     * @return {@code true} if the accessory extension should be displayed, {@code false} otherwise
     */
    default boolean isAccessoryExtensionVisible() {
        AccessoryScreenExtension extension = getAccessoryExtension();

        if (extension != null) {
            return extension.isVisible() && areAccessoryExtensionWidgetsVisible();
        }

        return false;
    }

    /**
     * A per-screen function that determines whether extensions widgets should be rendered
     * @return {@code true} if extension widgets should be rendered, {@code false} otherwise
     */
    default boolean areAccessoryExtensionWidgetsVisible() {
        AccessoryScreenExtension extension = getAccessoryExtension();

        if (extension != null) {
            return extension.areWidgetsVisible();
        }

        return false;
    }
}
