package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtensions;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
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
public final class AccessoryScreenExtensions {
    private static final Map<Identifier, AccessoryScreenExtension.Factory> SCREEN_EXTENSIONS = new HashMap<>();

    /**
     * Use this to bind a screen extension type to a menu extension type.
     * This must be done after calling {@link AccessoryMenuExtensions#register(Identifier, AccessoryMenuExtension.Factory)}
     * @param id identifier matching the menu extension registered
     * @param factory factory for the extension, will be constructed later
     */
    public static void register(Identifier id, AccessoryScreenExtension.Factory factory) {
        if (AccessoryMenuExtensions.exists(id)) {
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
     * A shortcut to {@link IAccessoryMenu#getAccessoryExtensionX()}
     * @param screen the accessory extension screen to query
     * @return relative x-coordinate to place the extension
     */
    public static int getAccessoryExtensionX(IAccessoryScreen screen) {
        AccessoryScreenExtension extension = screen.getAccessoryExtension();

        if (extension != null) {
            return extension.getMenuExtension().getAccessoryMenu().getAccessoryExtensionX();
        }

        return 0;
    }

    /**
     * A shortcut to {@link IAccessoryMenu#getAccessoryExtensionY()}
     * @param screen the accessory extension screen to query
     * @return relative y-coordinate to place the extension
     */
    public static int getAccessoryExtensionY(IAccessoryScreen screen) {
        AccessoryScreenExtension extension = screen.getAccessoryExtension();

        if (extension != null) {
            return extension.getMenuExtension().getAccessoryMenu().getAccessoryExtensionY();
        }

        return 0;
    }
}
