package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.client.ui.AccessoryUIs;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Holds methods related to screen extensions that implement correct functionality
 * <p>
 * In order for the extension to function properly, you should call:
 * <ul>
 *     <li>{@link #onConstruct(AbstractContainerScreen)}</li>
 * </ul>
 */
public final class AccessoryScreens {
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
                        slot.applyOffset(accessoryScreen.getAccessoryExtensionX().get(), accessoryScreen.getAccessoryExtensionY().get());
                    }

                    AccessoryScreenExtension extension = AccessoryUIs.getActiveScreenFactory().construct(screen, menuExtension);

                    accessoryScreen.setAccessoryExtension(extension);

                    if (OhmegaConfig.Client.getData().compatibilityMode().get() && accessoryScreen.isAccessoryExtensionVisible()) {
                        screen.imageWidth += extension.getExtraWidth();
                        screen.imageHeight += extension.getExtraHeight();
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
     * Retrieve the "effective" screen if possible.
     * In more detail, if the current forefront screen is an {@link IEmbeddingScreen}, will return {@link IEmbeddingScreen#getEmbeddedScreen()},
     * however if that fails, it will simply return {@link Minecraft#screen}
     * @return the use-effective accessory screen
     */
    public static Screen getEffectiveScreen() {
        Screen screen = Minecraft.getInstance().screen;

        if (screen instanceof IEmbeddingScreen embeddedScreen) {
            return embeddedScreen.getEmbeddedScreen();
        }

        return screen;
    }

    /**
     * Check whether the current effective screen is both an accessory screen and its widgets are visible.
     * This is here as to defer loading to prevent logical side load errors when running on a dedicated server
     * @return {@code true} if we should render accessory related widgets, {@code false} otherwise
     */
    public static boolean areExtensionWidgetsVisible() {
        Screen screen = getEffectiveScreen();

        if (screen instanceof IAccessoryScreen accessoryScreen) {
            return accessoryScreen.areAccessoryExtensionWidgetsVisible();
        }

        return false;
    }
}
