package com.swacky.ohmega.client.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IEmbeddingScreen;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.menu.AccessorySlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public final class EditUiScreen extends Screen implements IEmbeddingScreen {
    private final Screen parentScreen;
    private final AbstractContainerScreen<?> previewScreen;
    private final AccessoryScreenExtension screenExtension;
    private final AccessoryMenuExtension menuExtension;
    private final boolean originalVisibility;
    private final IntLazySavedValue xValue;
    private final IntLazySavedValue yValue;

    private boolean allowSetScreen = true;
    private CreativeModeTab previousTab = null;
    private SnapLine[] snapLines = {};
    private boolean shouldUseMagnetics = false;
    private boolean shouldShowLines = false;
    private boolean isExtensionFocused = false;
    private boolean isExtensionHeld = false;
    private int previousSetX = 0;
    private int previousSetY = 0;
    private double xo = 0;
    private double yo = 0;
    private int xSnapLineIndex = -1;
    private int ySnapLineIndex = -1;

    @SuppressWarnings("DataFlowIssue")
    public EditUiScreen(Screen parentScreen, LocalPlayer owner) {
        super(Component.translatable(Ohmega.MODID + ".configuration.edit_ui.title"));

        this.parentScreen = parentScreen;

        if (parentScreen instanceof IAccessoryScreen && parentScreen instanceof AbstractContainerScreen<?> containerScreen) {
            this.previewScreen = containerScreen;
        } else {
            if (owner.hasInfiniteMaterials()) {
                this.previewScreen = new CreativeModeInventoryScreen(owner, owner.connection.enabledFeatures(), minecraft.options.operatorItemsTab().get());
            } else {
                this.previewScreen = new InventoryScreen(owner);
            }
        }

        IAccessoryScreen accessoryScreen = (IAccessoryScreen) this.previewScreen;
        this.screenExtension = accessoryScreen.getAccessoryExtension();

        if (this.screenExtension != null) {
            this.menuExtension = screenExtension.getMenuExtension();
            this.originalVisibility = this.screenExtension.isVisible();

            this.screenExtension.setVisible(true);
        } else {
            this.menuExtension = null;
            this.originalVisibility = false;
        }

        this.xValue = accessoryScreen.getAccessoryExtensionX();
        this.yValue = accessoryScreen.getAccessoryExtensionY();
    }

    private boolean isHoveringExtension(double mx, double my) {
        if (screenExtension != null && screenExtension.isVisible()) {
            mx -= xValue.get() + previewScreen.leftPos;
            my -= yValue.get() + previewScreen.topPos;

            for (Rect2i rect : screenExtension.getRects()) {
                int rectX = rect.getX();
                int rectY = rect.getY();

                if (mx >= rectX && mx <= rectX + rect.getWidth() && my >= rectY && my <= rectY + rect.getHeight()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateSlotPositions() {
        if (menuExtension != null) {
            List<AccessorySlot> accessorySlots = menuExtension.getAccessoryMenu().getSlots();

            if (accessorySlots != null) {
                NonNullList<Slot> slots = menuExtension.getMenu().slots;

                for (AccessorySlot accessorySlot : accessorySlots) {
                    Slot slot = slots.get(accessorySlot.index);

                    slot.x = accessorySlot.getOriginalX() + xValue.get();
                    slot.y = accessorySlot.getOriginalY() + yValue.get();
                }
            }
        }
    }

    private void tryRenderSnapLine(GuiGraphicsExtractor gui, int index) {
        if (index != -1) {
            SnapLine line = snapLines[index];

            if (line.vertical) {
                gui.verticalLine(line.value(), 0, previewScreen.height, 0xbbff6666);
            } else {
                gui.horizontalLine(0, previewScreen.width, line.value(), 0xbbff6666);
            }
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        Optional<GuiEventListener> child = previewScreen.getChildAt(event.x(), event.y());

        if (child.isPresent()) {
            GuiEventListener widget = child.get();

            if (widget.mouseClicked(event, isDoubleClick) && widget.shouldTakeFocusAfterInteraction()) {
                setFocused(widget);

                if (event.button() == 0) {
                    setDragging(true);
                }
            }

            isExtensionFocused = false;

            return true;
        }

        if (event.button() == 0 && isHoveringExtension(event.x(), event.y())) {
            isExtensionFocused = true;
            isExtensionHeld = true;
            previousSetX = xValue.get();
            previousSetY = yValue.get();

            return true;
        }

        isExtensionFocused = false;
        return previewScreen.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (isExtensionHeld) {
            isExtensionHeld = false;
            xo = 0;
            yo = 0;
        }

        xSnapLineIndex = -1;
        ySnapLineIndex = -1;

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (isExtensionHeld) {
            xo += dx;
            yo += dy;
            int x = Math.clamp(previousSetX + (int) xo + previewScreen.leftPos, 0, previewScreen.width - screenExtension.getWidth() - 1);
            int y = Math.clamp(previousSetY + (int) yo + previewScreen.topPos, 0, previewScreen.height - screenExtension.getHeight() - 1);

            int snappedX = Integer.MAX_VALUE;
            int snappedY = Integer.MAX_VALUE;
            xSnapLineIndex = -1;
            ySnapLineIndex = -1;

            if (shouldUseMagnetics) {
                for (int i = 0; i < snapLines.length; i++) {
                    SnapLine line = snapLines[i];

                    if (line.vertical()) {
                        int testValue = line.test(x, screenExtension.getWidth());

                        if (testValue != -1 && Math.abs(x - testValue) < Math.abs(x - snappedX)) {
                            snappedX = testValue;
                            xSnapLineIndex = i;
                        }
                    } else {
                        int testValue = line.test(y, screenExtension.getHeight());

                        if (testValue != -1 && Math.abs(y - testValue) < Math.abs(y - snappedY)) {
                            snappedY = testValue;
                            ySnapLineIndex = i;
                        }
                    }
                }
            }

            if (xSnapLineIndex == -1) {
                xValue.set(x - previewScreen.leftPos);
            } else {
                xValue.set(snappedX - previewScreen.leftPos);
            }

            if (ySnapLineIndex == -1) {
                yValue.set(y - previewScreen.topPos);
            } else {
                yValue.set(snappedY - previewScreen.topPos);
            }
            updateSlotPositions();
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        previewScreen.extractRenderState(gui, mx, my, partialTicks);

        if (screenExtension != null && screenExtension.isVisible()) {
            boolean hoveringExtension = isHoveringExtension(mx, my);

            if (hoveringExtension) {
                gui.requestCursor(CursorTypes.POINTING_HAND);
            }

            int xo = xValue.get() + previewScreen.leftPos;
            int yo = yValue.get() + previewScreen.topPos;
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (Rect2i rect : screenExtension.getRects()) {
                int rectX = rect.getX();
                int rectY = rect.getY();
                int rectWidth = rect.getWidth();
                int rectHeight = rect.getHeight();

                if (hoveringExtension && (isExtensionHeld || previewScreen.getChildAt(mx, my).isEmpty())) {
                    gui.fill(rectX + xo, rectY + yo, rectX + rectWidth + xo, rectY + rectHeight + yo, 0x40ccccff);
                }

                if (isExtensionFocused) {
                    minX = Math.min(minX, rectX);
                    minY = Math.min(minY, rectY);
                    maxX = Math.max(maxX, rectX + rectWidth);
                    maxY = Math.max(maxY, rectY + rectHeight);
                }
            }

            if (isExtensionFocused) {
                gui.outline(minX + xo, minY + yo, maxX - minX, maxY - minY, 0xdd8888ff);

                if (shouldShowLines) {
                    minX += xo;
                    minY += yo;
                    maxX += xo;
                    maxY += yo;
                    int midX = (minX + maxX) / 2;
                    int midY = (minY + maxY) / 2;
                    int pixelDistance;

                    // Left
                    if (minX < previewScreen.leftPos || midY < previewScreen.topPos || midY > previewScreen.topPos + previewScreen.imageHeight) {
                        pixelDistance = minX;
                    } else {
                        if (minX < previewScreen.leftPos + previewScreen.imageWidth) {
                            pixelDistance = minX - previewScreen.leftPos;
                        } else {
                            pixelDistance = minX - previewScreen.leftPos - previewScreen.imageWidth;
                        }
                    }

                    if (pixelDistance > 0) {
                        gui.verticalLine(minX - 1, midY - 2, midY + 2, 0xdd8888ff);
                        gui.horizontalLine(minX - 2, minX - pixelDistance, midY, 0xdd8888ff);

                        if (pixelDistance >= 30) {
                            gui.text(minecraft.font, Component.literal(pixelDistance + "px"), minX - pixelDistance + 4, midY + 4, 0xdd8888ff);
                        }
                    }

                    // Top
                    if (minY < previewScreen.topPos || midX < previewScreen.leftPos || midX > previewScreen.leftPos + previewScreen.imageWidth) {
                        pixelDistance = minY;
                    } else {
                        if (minY < previewScreen.topPos + previewScreen.imageHeight) {
                            pixelDistance = minY - previewScreen.topPos;
                        } else {
                            pixelDistance = minY - previewScreen.topPos - previewScreen.imageHeight;
                        }
                    }

                    if (pixelDistance > 0) {
                        gui.horizontalLine(midX - 2, midX + 2, minY - 1, 0xdd8888ff);
                        gui.verticalLine(midX, minY, minY - pixelDistance, 0xdd8888ff);

                        if (pixelDistance >= 15) {
                            gui.text(minecraft.font, Component.literal(pixelDistance + "px"), midX + 4, minY - pixelDistance + 4, 0xdd8888ff);
                        }
                    }


                    // Right
                    if (maxX > previewScreen.leftPos + previewScreen.imageWidth || midY < previewScreen.topPos || midY > previewScreen.topPos + previewScreen.imageHeight) {
                        pixelDistance = previewScreen.width - maxX;
                    } else {
                        if (maxX < previewScreen.leftPos) {
                            pixelDistance = previewScreen.leftPos - maxX;
                        } else {
                            pixelDistance = previewScreen.leftPos + previewScreen.imageWidth - maxX;
                        }
                    }

                    if (pixelDistance > 0) {
                        gui.verticalLine(maxX, midY - 2, midY + 2, 0xdd8888ff);
                        gui.horizontalLine(maxX + 1, maxX + pixelDistance, midY, 0xdd8888ff);

                        if (pixelDistance >= 30) {
                            String string = pixelDistance + "px";

                            gui.text(minecraft.font, Component.literal(string), maxX + pixelDistance - minecraft.font.width(string) - 4, midY + 4, 0xdd8888ff);
                        }
                    }

                    // Top
                    if (maxY > previewScreen.topPos + previewScreen.imageHeight || midX < previewScreen.leftPos || midX > previewScreen.leftPos + previewScreen.imageWidth) {
                        pixelDistance = previewScreen.height - maxY;
                    } else {
                        if (maxY < previewScreen.topPos) {
                            pixelDistance = previewScreen.topPos - maxY;
                        } else {
                            pixelDistance = previewScreen.topPos + previewScreen.imageHeight - maxY;
                        }
                    }

                    if (pixelDistance > 0) {
                        gui.horizontalLine(midX - 2, midX + 2, maxY, 0xdd8888ff);
                        gui.verticalLine(midX, maxY + 1, maxY + pixelDistance, 0xdd8888ff);

                        if (pixelDistance >= 15) {
                            gui.text(minecraft.font, Component.literal(pixelDistance + "px"), midX + 4, maxY + pixelDistance - minecraft.font.lineHeight - 4, 0xdd8888ff);
                        }
                    }
                }
            }

            if (shouldUseMagnetics) {
                tryRenderSnapLine(gui, xSnapLineIndex);
                tryRenderSnapLine(gui, ySnapLineIndex);
            }
        }
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (OhmegaBinds.OPEN_EDIT_UI.matches(event)) {
            onClose();
            return true;
        }

        if (isExtensionFocused) {
            if (OhmegaBinds.EDIT_MAGNETICS.matches(event)) {
                shouldUseMagnetics = true;
            }

            if (OhmegaBinds.EDIT_NUDGE_LEFT.matches(event)) {
                int x = xValue.get();

                if (x + previewScreen.leftPos > 0) {
                    xValue.set(x - 1);
                    updateSlotPositions();
                    return true;
                }
            }

            if (OhmegaBinds.EDIT_NUDGE_UP.matches(event)) {
                int y = yValue.get();

                if (y + previewScreen.topPos > 0) {
                    yValue.set(y - 1);
                    updateSlotPositions();
                    return true;
                }
            }

            if (OhmegaBinds.EDIT_NUDGE_RIGHT.matches(event)) {
                int x = xValue.get();

                if (x + screenExtension.getWidth() + previewScreen.leftPos + 1 < previewScreen.width) {
                    xValue.set(x + 1);
                    updateSlotPositions();
                    return true;
                }
            }

            if (OhmegaBinds.EDIT_NUDGE_DOWN.matches(event)) {
                int y = yValue.get();

                if (y + screenExtension.getHeight() + previewScreen.topPos + 1 < previewScreen.height) {
                    yValue.set(y + 1);
                    updateSlotPositions();
                    return true;
                }
            }

            if (OhmegaBinds.EDIT_SHOW_LINES.matches(event)) {
                shouldShowLines = true;
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        if (OhmegaBinds.EDIT_MAGNETICS.matches(event)) {
            shouldUseMagnetics = false;
            return true;
        }

        if (OhmegaBinds.EDIT_SHOW_LINES.matches(event)) {
            shouldShowLines = false;
            return true;
        }

        return super.keyReleased(event);
    }

    @Override
    public void onClose() {
        if (screenExtension != null) {
            screenExtension.setVisible(originalVisibility);
        }

        xValue.serialise();
        yValue.serialise();

        if (previousTab != null && previewScreen instanceof CreativeModeInventoryScreen screen) {
            screen.selectTab(previousTab);
        }

        if (parentScreen instanceof AbstractContainerScreen<?>) {
            super.onClose();
        } else {
            minecraft.gui.setScreen(parentScreen);
        }
    }

    @Override
    public void init() {
        Window window = minecraft.getWindow();
        allowSetScreen = false;
        previewScreen.init(window.getGuiScaledWidth(), window.getGuiScaledHeight());
        allowSetScreen = true;

        if (previewScreen instanceof CreativeModeInventoryScreen screen) {
            previousTab = CreativeModeInventoryScreen.selectedTab;

            for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
                if (tab.getType() == CreativeModeTab.Type.INVENTORY) {
                    screen.selectTab(tab);
                    break;
                }
            }
        }

        snapLines = new SnapLine[]{
                new SnapLine(true, previewScreen.width / 2),
                new SnapLine(false, previewScreen.height / 2),
                new SnapLine(true, previewScreen.leftPos),
                new SnapLine(false, previewScreen.topPos),
                new SnapLine(true, previewScreen.leftPos + previewScreen.imageWidth),
                new SnapLine(false, previewScreen.topPos + previewScreen.imageHeight),
        };
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        previewScreen.extractBackground(gui, mx, my, partialTicks);
    }

    @Override
    protected void extractBlurredBackground(@NonNull GuiGraphicsExtractor gui) {}

    @Override
    public void extractTransparentBackground(@NonNull GuiGraphicsExtractor gui) {
        previewScreen.extractTransparentBackground(gui);
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public @NonNull Screen getEmbeddedScreen() {
        return previewScreen;
    }

    @Override
    public boolean shouldAllowSetScreen() {
        return allowSetScreen;
    }

    private record SnapLine(boolean vertical, int value) {
        private int test(int testValue, int delta) {
            int startDistance = Math.abs(testValue - value);
            int centreDistance = Math.abs(testValue + delta / 2 - value);
            int endDistance = Math.abs(testValue + delta - value);
            int closestDistance = Integer.MAX_VALUE;
            int returnValue = -1;

            if (startDistance < 6) {
                closestDistance = startDistance;
                returnValue = value;
            }

            if (centreDistance < 6 && centreDistance < closestDistance) {
                closestDistance = centreDistance;
                returnValue = value - delta / 2;
            }

            if (endDistance < 6 && endDistance < closestDistance) {
                returnValue = value - delta;
            }

            return returnValue;
        }
    }
}
