package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.common.menu.AccessorySlot;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

// todo: clear added slots and replace with new ones on config change
// todo: if possible, instead move ALL of this to a client-based approach with only one registration needed
/**
 * Implemented by {@link AbstractContainerMenu}s to allow them to have an accessory extension.
 * See {@link AccessoryMenus} for crucial implementation details
 * <p>
 * This also contains some utility methods which you <strong>should</strong> call to implement complete functionality,
 * failing to call them may cause your menu to not have an extension applied
 * <p>
 * By default, this is applied by Ohmega via mixin to:
 * <ul>
 *     <li>{@link CreativeModeInventoryScreen.ItemPickerMenu}</li>
 *     <li>{@link InventoryMenu}</li>
 * </ul>
 */
public interface IAccessoryMenu {
    /**
     * Retrieve the accessory extension bound to this menu.
     * If using this, you should almost always check for nullability
     * @return bound accessory extension
     */
    @Nullable AccessoryMenuExtension getAccessoryExtension();

    /**
     * Set the accessory extension bound to this menu.
     * This is called in {@link AccessoryMenus#onConstruct(AbstractContainerMenu, Player)} (which you should be calling) for you
     * @param extension accessory extension to set to
     */
    void setAccessoryExtension(@NonNull AccessoryMenuExtension extension);

    /**
     * Get a list of the accessory slots added to the extension's parent menu.
     * This is stored as to eliminate the need for looping through all the slots just to perform operations on our custom ones
     * @return a list of strictly {@link AccessorySlot}s added with the accessory extension
     */
    default @Nullable List<AccessorySlot> getSlots() {
        AccessoryMenuExtension extension = getAccessoryExtension();

        if (extension != null) {
            return extension.getSlots();
        }

        return null;
    }

    /**
     * Determines whether the extension should be shown
     * @return {@code true} if the accessory extension should be shown, {@code false} otherwise
     */
    default boolean isAccessoryExtensionVisible() {
        AccessoryMenuExtension extension = getAccessoryExtension();

        if (extension != null) {
            return extension.isVisible();
        }

        return false;
    }

    /**
     * Set the visibility of the extension
     * @param value {@code true} to make the extension visible, {@code false} to hide it
     */
    default void setAccessoryExtensionVisible(boolean value) {
        AccessoryMenuExtension extension = getAccessoryExtension();

        if (extension != null) {
            extension.setVisible(value);
        }
    }
}
