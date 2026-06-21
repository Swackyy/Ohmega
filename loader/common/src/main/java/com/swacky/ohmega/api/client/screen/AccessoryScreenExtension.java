package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.client.screen.widget.IEditUiElement;
import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A way to add extra slots and functionality to the default inventory.
 * This does not override any vanilla behaviour such as inventory slots, it is purely an extension
 * <p>
 * Ohmega uses this to provide the default accessory extension implementation packaged with the mod
 */
public abstract class AccessoryScreenExtension implements IEditUiElement {
    private final @NonNull AbstractContainerScreen<?> screen;
    private final @NonNull IAccessoryScreen accessoryScreen;
    private final @NonNull AccessoryMenuExtension menuExtension;
    private final @NonNull List<AbstractWidget> overlayWidgets = new ArrayList<>();

    /**
     * Constructs an instance of this screen extension, called internally by Ohmega.
     * Your constructor of this should be what you pass to {@link AccessoryExtensions#registerExtension(Identifier, AccessoryMenuExtension.Factory, Factory)}
     * as the screen factory, to be constructed when necessary
     * @param screen the parent screen
     * @param menuExtension the corresponding parent menu's {@link AccessoryMenuExtension}
     */
    public AccessoryScreenExtension(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryMenuExtension menuExtension) {
        this.screen = screen;
        this.accessoryScreen = (IAccessoryScreen) screen;
        this.menuExtension = menuExtension;
    }

    /**
     * Retrieve the parent screen for this accessory extension instance
     * @return the parent screen where the extension will be added onto
     */
    public @NonNull AbstractContainerScreen<?> getScreen() {
        return screen;
    }

    /**
     * Get the {@link IAccessoryScreen} cast version of {@link #getScreen()}
     * @return the parent {@link IAccessoryScreen} this extension belongs to
     */
    public @NonNull IAccessoryScreen getAccessoryScreen() {
        return accessoryScreen;
    }

    /**
     * Get the corresponding parent screen's menu extension
     * @return the parent menu's {@link AccessoryScreenExtension}
     */
    public @NonNull AccessoryMenuExtension getMenuExtension() {
        return menuExtension;
    }

    /**
     * Gets the list of widgets to render as an overlay, above (usually) all other rendering.
     * These are added through {@link #initExtension(WidgetAdder)} by calling {@link WidgetAdder#addOverlay(AbstractWidget)}
     * @return list of widgets to render after most other things
     */
    public final @NonNull List<AbstractWidget> getOverlayWidgets() {
        return overlayWidgets;
    }

    /**
     * Get the width of the extension
     * @return width of this accessory extension
     */
    public abstract int getWidth();

    /**
     * Get the height of the extension
     * @return height of this accessory extension
     */
    public abstract int getHeight();

    /**
     * Used to add widgets or execute other setup code for the screen, equivalent to {@link AbstractContainerScreen#init()}
     * @param adder a utility to add widgets to the GUI
     */
    public abstract void initExtension(WidgetAdder adder);

    /**
     * Extract render data for the accessory extension, called in {@link AbstractContainerScreen#extractBackground(GuiGraphicsExtractor, int, int, float)}.
     * The current pose is relative to the user's (adjusted) defined co-ordinates for the extension
     * @param gui graphics extractor
     */
    public abstract void extractExtension(GuiGraphicsExtractor gui);

    /**
     * Check if the player has clicked outside the accessory extension bounds.
     * Parameters are relative to the top left of the extension, not the parent screen
     * <p>
     * Automatically implemented for {@link AbstractContainerScreen} and {@link AbstractRecipeBookScreen}
     * @param mx mouse x relative to the extension
     * @param my mouse y relative to the extension
     * @return {@code true} if the player is found to have clicked outside the extension's bounds, {@code false} otherwise
     */
    public abstract boolean hasClickedOutside(double mx, double my);

    /**
     * Get the actual width added with the extension on the left side
     * @return total width that lies over the current screen's leftmost boundary
     */
    public final int getExtraWidthLeft() {
        return Math.clamp(-accessoryScreen.getAccessoryExtensionPosition().x().get(), 0, getWidth());
    }

    /**
     * Get the actual width added with the extension on the right side
     * @return total width that lies over the current screen's rightmost boundary
     */
    public final int getExtraWidthRight() {
        int width = getWidth();

        return Math.clamp(accessoryScreen.getAccessoryExtensionPosition().x().get() + width - screen.imageWidth, 0, width);
    }

    /**
     * Get the actual height added with the extension on the top side
     * @return total height that lies over the current screen's topmost boundary
     */
    public final int getExtraHeightTop() {
        return Math.clamp(-accessoryScreen.getAccessoryExtensionPosition().y().get(), 0, getHeight());
    }

    /**
     * Get the actual height added with the extension on the bottom side
     * @return total height that lies over the current screen's bottommost boundary
     */
    public final int getExtraHeightBottom() {
        int height = getHeight();

        return Math.clamp(accessoryScreen.getAccessoryExtensionPosition().y().get() + height - screen.imageHeight, 0, height);
    }

    /**
     * Get the actual width added with the extension
     * @return total width that lies over the current screen's boundaries
     */
    // todo: some references to this are wrong
    public final int getExtraWidth() {
        return getExtraWidthLeft() + getExtraWidthRight();
    }

    /**
     * Get the actual height added with the extension
     * @return total height that lies over the current screen's boundaries
     */
    // todo: some references to this are wrong
    public final int getExtraHeight() {
        return getExtraHeightTop() + getExtraHeightBottom();
    }

    /**
     * A per-extension function that determines whether the extension should be shown.
     * If you want to check this, call {@link IAccessoryScreen#isAccessoryExtensionVisible()} instead
     * @return {@code true} if the accessory extension should be displayed, {@code false} otherwise
     */
    public final boolean isVisible() {
        return menuExtension.getAccessoryMenu().isAccessoryExtensionVisible();
    }

    /**
     * A defaulted empty method that is called after {@link #setVisible(boolean)} is invoked
     * @param value {@code true} if the extension has been set to visible, {@code false} if it has been hidden
     */
    protected void onSetVisible(boolean value) {}

    /**
     * Change the visibility of the accessory extension
     * @param value {@code true} to make the extension visible, {@code false} to hide it
     */
    public final void setVisible(boolean value) {
        if (OhmegaConfig.Client.getData().compatibilityMode().get() && menuExtension.getAccessoryMenu().isAccessoryExtensionVisible() != value) {
            if (value) {
                screen.imageWidth += getExtraWidth();
                screen.imageHeight += getExtraHeight();
            } else {
                screen.imageWidth -= getExtraWidth();
                screen.imageHeight -= getExtraHeight();
            }

            screen.init(screen.width, screen.height);
        }

        menuExtension.getAccessoryMenu().setAccessoryExtensionVisible(value);
        onSetVisible(value);
    }

    /**
     * A way to dictate whether we should render extension widgets.
     * The visibility of the extension itself also relies on this
     * @return {@code true} if extension widgets should be rendered, {@code false} otherwise
     */
    public boolean areWidgetsVisible() {
        return true;
    }

    @Override
    public final @NonNull LazyPosition getElementPosition() {
        return accessoryScreen.getAccessoryExtensionPosition();
    }

    @Override
    public final boolean isActive() {
        return accessoryScreen.isAccessoryExtensionVisible();
    }

    /**
     * A helper class to easily add widgets to the parent screen of the extension
     */
    public static final class WidgetAdder {
        private final @NonNull Consumer<AbstractWidget> consumer;
        private final @NonNull List<AbstractWidget> overlayWidgets;

        /**
         * Creates a new instance, called internally
         * @param consumer a callback to add widgets
         * @param overlayWidgets a list of widgets that should be added, being rendered over the top of other widgets and most other rendering
         */
        public WidgetAdder(@NonNull Consumer<AbstractWidget> consumer, @NonNull List<AbstractWidget> overlayWidgets) {
            this.consumer = consumer;
            this.overlayWidgets = overlayWidgets;
        }

        /**
         * Simply add a widget to the screen
         * @param widget generic widget to add
         */
        public void add(@NonNull AbstractWidget widget) {
            consumer.accept(widget);
        }

        /**
         * Simply add a widget to the screen that will be rendered after (usually) everything else
         * @param widget generic widget to add
         */
        public void addOverlay(@NonNull AbstractWidget widget) {
            overlayWidgets.add(widget);
        }
    }

    public interface Factory {
        @NonNull AccessoryScreenExtension construct(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryMenuExtension menuExtension);
    }
}
