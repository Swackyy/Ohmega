package com.swacky.ohmega.api.client.screen.widget;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface IEditUiElement {
    @NonNull LazyPosition getElementPosition();

    int getWidth();

    int getHeight();

    boolean isActive();

    default boolean isExtensionRelative() {
        return false;
    }

    default @NonNull List<Rect2i> getRects() {
        return List.of(new Rect2i(
                0,
                0,
                getWidth(),
                getHeight()));
    }

    default @NonNull List<SnapLine> getSnapLines(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryScreenExtension screenExtension) {
        if (isExtensionRelative()) {
            LazyPosition position = screenExtension.getElementPosition();
            int x = screen.leftPos + position.x().get();
            int y = screen.topPos + position.y().get();

            return List.of(
                    new SnapLine(true, x),
                    new SnapLine(false, y),
                    new SnapLine(true, x + screenExtension.getWidth()),
                    new SnapLine(false, y + screenExtension.getHeight()));
        }

        return List.of(
                new SnapLine(true, screen.width / 2),
                new SnapLine(false, screen.height / 2),
                new SnapLine(true, screen.leftPos),
                new SnapLine(false, screen.topPos),
                new SnapLine(true, screen.leftPos + screen.imageWidth),
                new SnapLine(false, screen.topPos + screen.imageHeight));
    }

    // Right-click submenu options?
}
