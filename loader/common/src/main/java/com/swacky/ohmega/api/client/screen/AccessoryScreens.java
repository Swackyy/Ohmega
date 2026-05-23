package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds known screen extension factories and common methods related to screen extensions
 * <p>
 * In order for your extension to function properly, you should call:
 * <ul>
 *     <li>{@link #onConstruct(AbstractContainerScreen)}</li>
 * </ul>
 */
public final class AccessoryScreens {
    private static final Map<Identifier, AccessoryScreenExtension.Factory> SCREEN_EXTENSIONS = new HashMap<>();

    /**
     * Use this to bind a screen extension type to a menu extension type.
     * This must be done after calling {@link AccessoryMenus#registerExtension(Identifier, AccessoryMenuExtension.Factory)}
     * @param id identifier matching the menu extension registered
     * @param factory factory for the extension, will be constructed later
     */
    public static void registerExtension(Identifier id, AccessoryScreenExtension.Factory factory) {
        if (AccessoryMenus.extensionExists(id)) {
            if (!SCREEN_EXTENSIONS.containsKey(id)) {
                SCREEN_EXTENSIONS.put(id, factory);
            }
        } else {
            throw new IllegalStateException("Failed to register accessory screen extension with identifier '" + id + "' as a corresponding accessory menu extension has not been registered");
        }
    }

    /**
     * Retrieve the active screen extension factory by parsing the server config value to an {@link Identifier}
     * @return the currently in-use screen extension factory
     */
    public static AccessoryScreenExtension.@Nullable Factory getActiveFactory() {
        return SCREEN_EXTENSIONS.get(Identifier.tryParse(OhmegaConfig.Server.menuExtensionId()));
    }

    /**
     * This must be called at the end of your target screen's constructor to assign the accessory extension and add slots
     * @param screen parent screen
     */
    public static void onConstruct(AbstractContainerScreen<?> screen) {
        if (screen instanceof IAccessoryScreen accessoryScreen) {
            AbstractContainerMenu menu = screen.getMenu();

            if (menu instanceof IAccessoryMenu accessoryMenu) {
                AccessoryMenuExtension menuExtension = accessoryMenu.getAccessoryExtension();

                if (menuExtension != null) {
                    for (AccessorySlot slot : menuExtension.getSlots()) {
                        slot.applyOffset(accessoryScreen.getAccessoryExtensionX(), accessoryScreen.getAccessoryExtensionY());
                    }

                    AccessoryScreenExtension.Factory factory = getActiveFactory();

                    if (factory != null) {
                        AccessoryScreenExtension extension = factory.construct(screen, menuExtension);

                        accessoryScreen.setAccessoryExtension(extension);

                        if (OhmegaConfig.Client.compatibilityMode() && accessoryScreen.isAccessoryExtensionVisible()) {
                            screen.imageWidth += extension.getExtraWidth();
                            screen.imageHeight += extension.getExtraHeight();
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Menu " + menu + " does not implement " + IAccessoryMenu.class);
            }
        } else {
            throw new IllegalArgumentException("Screen " + screen + " does not implement " + IAccessoryScreen.class);
        }
    }

    /**
     * Check whether the current screen is both an accessory screen and its widgets are visible.
     * This is here as to defer loading to prevent logical side load errors when running on a dedicated server
     * @return {@code true} if we should render accessory related widgets, {@code false} otherwise
     */
    public static boolean areExtensionWidgetsVisible() {
        if (Minecraft.getInstance().screen instanceof IAccessoryScreen accessoryScreen) {
            return accessoryScreen.areAccessoryExtensionWidgetsVisible();
        }

        return false;
    }
}
