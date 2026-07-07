package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Holds methods related to screen extensions that implement correct functionality
 * @apiNote In order for the extension to function properly, you should call:
 * <ul>
 *     <li>{@link #onConstruct(AbstractContainerScreen)}</li>
 * </ul>
 */
public final class AccessoryScreens {
    /**
     * Asserts that the passed {@link AbstractContainerScreen} implements {@link IAccessoryScreen}, otherwise {@code throw}s
     * @param screen vanilla screen instance to assert
     * @return the cast {@link IAccessoryScreen}
     */
    public static @NonNull IAccessoryScreen assertImplementation(@NonNull AbstractContainerScreen<?> screen) {
        if (screen instanceof IAccessoryScreen accessoryScreen) {
            return accessoryScreen;
        } else {
            throw new IllegalArgumentException("Screen " + screen.getClass().getCanonicalName() + " does not implement " + IAccessoryScreen.class.getCanonicalName());
        }
    }

    /**
     * Applies the offset to the {@link AccessorySlot}s so they appear in the correct place in the inventory
     * @param accessoryMenu parent menu as an {@link IAccessoryMenu}
     * @param accessoryScreen parent screen as an {@link IAccessoryScreen}
     */
    public static void applySlotOffsets(@NonNull IAccessoryMenu accessoryMenu, @NonNull IAccessoryScreen accessoryScreen) {
        List<AccessorySlot> slots = accessoryMenu.getSlots();

        if (slots != null) {
            LazyPosition position = accessoryScreen.getAccessoryExtensionPosition();

            for (AccessorySlot slot : slots) {
                slot.applyOffset(position.x().get(), position.y().get());
            }
        }
    }

    /**
     * Called by {@link #onConstruct(AbstractContainerScreen)} and will usually not need to be invoked manually
     * @param screen parent screen
     * @param menuExtension the {@link AccessoryMenuExtension} attached to the parent screen's menu
     * @param accessoryScreen cast version of the {@code screen} to {@link IAccessoryScreen}
     * @return the screen extension that has been attached to the given {@link IAccessoryScreen}
     */
    public static AccessoryScreenExtension attachExtension(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryMenuExtension menuExtension, @NonNull IAccessoryScreen accessoryScreen) {
        AccessoryScreenExtension extension = AccessoryExtensions.getActiveScreenFactory().construct(screen, menuExtension);

        accessoryScreen.setAccessoryExtension(extension);
        return extension;
    }

    /**
     * This must be called at the end of your target screen's constructor to assign the accessory extension and add slots.
     * The logic handled here is split into separate functions above because it may be useful at times to only run parts of the construction
     * @param screen parent screen
     */
    public static void onConstruct(@NonNull AbstractContainerScreen<?> screen) {
        IAccessoryMenu accessoryMenu = AccessoryMenus.assertImplementation(screen.getMenu());
        IAccessoryScreen accessoryScreen = assertImplementation(screen);
        AccessoryMenuExtension menuExtension = accessoryMenu.getAccessoryExtension();

        if (menuExtension != null) {
            applySlotOffsets(accessoryMenu, accessoryScreen);

            AccessoryScreenExtension extension = attachExtension(screen, menuExtension, accessoryScreen);

            if (OhmegaConfig.Client.getData().compatibilityMode().get() && accessoryScreen.isAccessoryExtensionVisible()) {
                screen.imageWidth += extension.getExtraWidth();
                screen.imageHeight += extension.getExtraHeight();
            }
        }
    }

    /**
     * Retrieve the "effective" screen if possible.
     * In more detail, if the current forefront screen is an {@link IEmbeddingScreen}, will return {@link IEmbeddingScreen#getEmbeddedScreen()},
     * however if that fails, it will simply return {@link Gui#screen()}
     * @return the use-effective accessory screen
     */
    public static @Nullable Screen getEffectiveScreen() {
        Screen screen = Minecraft.getInstance().gui.screen();

        if (screen instanceof IEmbeddingScreen embeddedScreen) {
            return embeddedScreen.getEmbeddedScreen();
        }

        return screen;
    }

    /**
     * Initialises certain fields and other data that would usually be performed in {@link Screen#init()}
     * @param screen parent screen
     * @param extension the {@link AccessoryScreenExtension} attached to the parent screen
     * @param consumer a callback used to add widgets to the parent screen
     */
    public static void doExtensionInit(Screen screen, AccessoryScreenExtension extension, Consumer<AbstractWidget> consumer) {
        List<AbstractWidget> overlayWidgets = extension.getOverlayWidgets();

        overlayWidgets.clear();
        extension.initExtension(new AccessoryScreenExtension.WidgetAdder(consumer, overlayWidgets));

        for (AbstractWidget widget : overlayWidgets) {
            screen.children.add(widget);
            screen.narratables.add(widget);
        }
    }

    /**
     * Called when slots are re-built, used to realign the slots and other renderables
     * @param accessoryMenu parent menu as an {@link IAccessoryMenu}
     */
    public static void onRebuildSlots(IAccessoryMenu accessoryMenu) {
        if (AccessoryScreens.getEffectiveScreen() instanceof AbstractContainerScreen<?> screen) {
            IAccessoryScreen accessoryScreen = AccessoryScreens.assertImplementation(screen);

            AccessoryScreens.applySlotOffsets(accessoryMenu, accessoryScreen);

            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null) {
                doExtensionInit(screen, extension, screen::addRenderableWidget);
            }
        }

        ClientCallbacks.reloadRegisteredKeybinds(Minecraft.getInstance().options::load);
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
