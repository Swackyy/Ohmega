package com.swacky.ohmega.client.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IEmbeddingScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.client.screen.SnapLine;
import com.swacky.ohmega.api.client.screen.widget.IEditUiElement;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class EditUiScreen extends Screen implements IEmbeddingScreen {
    private static final Identifier BACKGROUND_LOCATION = Ohmega.id("textures/gui/sprites/edit_ui_background.png");

    private final Screen parentScreen;
    private final AbstractContainerScreen<?> embeddedScreen;
    private final AccessoryScreenExtension screenExtension;
    private final AccessoryMenuExtension menuExtension;
    private final boolean originalVisibility;
    private final long startMillis;
    private final Set<IEditUiElement> mutatedElements = new HashSet<>();
    private final Deque<Pair<IEditUiElement, IntIntPair>> undoDequeue = new ArrayDeque<>();
    private final Deque<Pair<IEditUiElement, IntIntPair>> redoDequeue = new ArrayDeque<>();

    private boolean allowSetScreen = true;
    private boolean allowDimBackground = true;
    private CreativeModeTab previousTab = null;
    private IEditUiElement element = null;
    private boolean shouldUseMagnetics = false;
    private boolean shouldShowLines = false;
    private List<SnapLine> snapLines = List.of();
    private boolean isElementHeld = false;
    private SnapLine xSnapLine = null;
    private SnapLine ySnapLine = null;
    private boolean recordNudgeHistory = true;
    private int previousSetX = 0;
    private int previousSetY = 0;
    private double cumulativeXo = 0;
    private double cumulativeYo = 0;

    @SuppressWarnings("DataFlowIssue")
    public EditUiScreen(Screen parentScreen, LocalPlayer owner) {
        super(Component.translatable(Ohmega.MODID + ".configuration.edit_ui.title"));

        this.parentScreen = parentScreen;

        if (parentScreen instanceof IAccessoryScreen && parentScreen instanceof AbstractContainerScreen<?> containerScreen) {
            this.embeddedScreen = containerScreen;
        } else {
            if (owner.hasInfiniteMaterials()) {
                this.embeddedScreen = new CreativeModeInventoryScreen(owner, owner.connection.enabledFeatures(), minecraft.options.operatorItemsTab().get());
            } else {
                this.embeddedScreen = new InventoryScreen(owner);
            }
        }

        IAccessoryScreen accessoryScreen = (IAccessoryScreen) this.embeddedScreen;
        this.screenExtension = accessoryScreen.getAccessoryExtension();

        if (this.screenExtension != null) {
            this.menuExtension = screenExtension.getMenuExtension();
            this.originalVisibility = this.screenExtension.isVisible();

            this.screenExtension.setVisible(true);
        } else {
            this.menuExtension = null;
            this.originalVisibility = false;
        }

        startMillis = Util.getMillis();
    }

    private void extractHighlight(GuiGraphicsExtractor gui, IEditUiElement element, double mx, double my) {
        LazyPosition position = element.getElementPosition();
        int xo = position.x().get() + embeddedScreen.leftPos;
        int yo = position.y().get() + embeddedScreen.topPos;

        if (element.isExtensionRelative()) {
            LazyPosition extensionPosition = screenExtension.getElementPosition();
            xo += extensionPosition.x().get();
            yo += extensionPosition.y().get();
        }

        for (Rect2i rect : element.getRects()) {
            int rectX = rect.getX();
            int rectY = rect.getY();
            Optional<GuiEventListener> hoveringChild = embeddedScreen.getChildAt(mx, my);

            if (!isElementHeld || hoveringChild.isEmpty() || hoveringChild.get() == element) {
                gui.fill(rectX + xo, rectY + yo, rectX + rect.getWidth() + xo, rectY + rect.getHeight() + yo, 0x40ccccff);
            }
        }
    }

    private void extractSnapLine(GuiGraphicsExtractor gui, SnapLine line) {
        if (line != null) {
            if (line.vertical()) {
                gui.verticalLine(line.value(), 0, embeddedScreen.height, 0xbbff6666);
            } else {
                gui.horizontalLine(0, embeddedScreen.width, line.value(), 0xbbff6666);
            }
        }
    }

    private boolean isHoveringElement(IEditUiElement element, double mx, double my) {
        LazyPosition position = element.getElementPosition();

        mx -= embeddedScreen.leftPos + position.x().get();
        my -= embeddedScreen.topPos + position.y().get();

        if (element.isExtensionRelative()) {
            position = screenExtension.getElementPosition();

            mx -= position.x().get();
            my -= position.y().get();
        }

        for (Rect2i rect : element.getRects()) {
            int rectX = rect.getX();
            int rectY = rect.getY();

            if (mx >= rectX && mx <= rectX + rect.getWidth() && my >= rectY && my <= rectY + rect.getHeight()) {
                return true;
            }
        }

        return false;
    }

    private IEditUiElement getHoveringElement(double mx, double my) {
        for (AbstractWidget widget : screenExtension.getOverlayWidgets()) {
            if (widget instanceof IEditUiElement candidate && isHoveringElement(candidate, mx, my)) {
                return candidate;
            }
        }

        for (GuiEventListener child : getEmbeddedScreen().children()) {
            if (child instanceof IEditUiElement candidate && isHoveringElement(candidate, mx, my)) {
                return candidate;
            }
        }

        if (isHoveringElement(screenExtension, mx, my)) {
            return screenExtension;
        }

        return null;
    }

    private void doNudge(IntLazySavedValue value, boolean positive) {
        mutatedElements.add(element);

        LazyPosition position = element.getElementPosition();

        if (recordNudgeHistory) {
            recordNudgeHistory = false;

            pushDequeue(undoDequeue, IntIntPair.of(position.x().get(), position.y().get()));
        }

        if (positive) {
            value.set(value.get() + 1);
        } else {
            value.set(value.get() - 1);
        }

        tryUpdateSlotPositions();
    }

    private boolean nudge(boolean vertical, boolean positive) {
        LazyPosition position = element.getElementPosition();

        if (vertical) {
            IntLazySavedValue yPosition = position.y();
            int y = yPosition.get();

            if (positive) {
                if (y + embeddedScreen.topPos + element.getHeight() + 1 < embeddedScreen.height) {
                    doNudge(yPosition, true);
                    return true;
                }
            } else {
                if (y + embeddedScreen.topPos > 0) {
                    doNudge(yPosition, false);
                    return true;
                }
            }
        } else {
            IntLazySavedValue xPosition = position.x();
            int x = xPosition.get();

            if (positive) {
                if (x + embeddedScreen.leftPos + element.getWidth() + 1 < embeddedScreen.width) {
                    doNudge(xPosition, true);
                    return true;
                }
            } else {
                if (x + embeddedScreen.leftPos > 0) {
                    doNudge(xPosition, false);
                    return true;
                }
            }
        }

        return false;
    }

    private void pushDequeue(Deque<Pair<IEditUiElement, IntIntPair>> deque, IntIntPair oldPosition) {
        if (deque.size() >= 64) {
            deque.removeLast();
        }

        deque.push(Pair.of(element, oldPosition));
    }

    private void releaseElement() {
        if (element != null) {
            LazyPosition position = element.getElementPosition();

            if (previousSetX != position.x().get() || previousSetY != position.y().get()) {
                pushDequeue(undoDequeue, IntIntPair.of(previousSetX, previousSetY));
            }
        }

        isElementHeld = false;
        xSnapLine = null;
        ySnapLine = null;
        cumulativeXo = 0;
        cumulativeYo = 0;
    }

    private void setElementFocus(IEditUiElement element) {
        mutatedElements.add(element);

        this.element = element;
        snapLines = element.getSnapLines(embeddedScreen, screenExtension);
        recordNudgeHistory = true;
        LazyPosition position = element.getElementPosition();
        previousSetX = position.x().get();
        previousSetY = position.y().get();
    }

    private void tryUpdateSlotPositions() {
        if (element == screenExtension && menuExtension != null) {
            AccessoryScreens.applySlotOffsets(menuExtension.getAccessoryMenu(), screenExtension.getAccessoryScreen());
        }
    }

    private boolean updateHistory(Pair<IEditUiElement, IntIntPair> event, Deque<Pair<IEditUiElement, IntIntPair>> oppositeDequeue) {
        if (event != null) {
            setElementFocus(event.getLeft());

            LazyPosition position = element.getElementPosition();

            pushDequeue(oppositeDequeue, IntIntPair.of(position.x().get(), position.y().get()));

            IntIntPair newPosition = event.getRight();

            position.set(newPosition.firstInt(), newPosition.secondInt());
            tryUpdateSlotPositions();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        if (isDoubleClick) {
            Optional<GuiEventListener> child = embeddedScreen.getChildAt(event.x(), event.y());

            if (child.isPresent()) {
                GuiEventListener widget = child.get();

                if (widget.mouseClicked(event, false) && widget.shouldTakeFocusAfterInteraction()) {
                    setFocused(widget);

                    if (event.button() == 0) {
                        setDragging(true);
                    }
                }

                element = null;

                return true;
            }
        }

        if (event.button() == 0) {
            IEditUiElement candidate = getHoveringElement(event.x(), event.y());

            if (candidate != null && candidate.getElementPosition().isSerialisable()) {
                isElementHeld = true;

                setElementFocus(candidate);
                return true;
            }
        }

        element = null;

        releaseElement();
        return embeddedScreen.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        releaseElement();
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (isElementHeld) {
            cumulativeXo += dx;
            cumulativeYo += dy;

            int xo;
            int yo;

            if (element.isExtensionRelative()) {
                LazyPosition position = screenExtension.getElementPosition();
                xo = position.x().get();
                yo = position.y().get();
            } else {
                xo = 0;
                yo = 0;
            }

            int x = Math.clamp(previousSetX + (int) cumulativeXo + embeddedScreen.leftPos, -xo, embeddedScreen.width - element.getWidth() - xo - 1);
            int y = Math.clamp(previousSetY + (int) cumulativeYo + embeddedScreen.topPos, -yo, embeddedScreen.height - element.getHeight() - yo - 1);
            int testX = x + xo;
            int testY = y + yo;
            int snappedX = Integer.MAX_VALUE;
            int snappedY = Integer.MAX_VALUE;
            xSnapLine = null;
            ySnapLine = null;

            if (shouldUseMagnetics) {
                for (SnapLine line : snapLines) {
                    if (line.vertical()) {
                        int testValue = line.test(testX, element.getWidth());

                        if (testValue != -1 && Math.abs(testX - testValue) < Math.abs(testX - snappedX)) {
                            snappedX = testValue - xo;
                            xSnapLine = line;
                        }
                    } else {
                        int testValue = line.test(testY, element.getHeight());

                        if (testValue != -1 && Math.abs(testY - testValue) < Math.abs(testY - snappedY)) {
                            snappedY = testValue - yo;
                            ySnapLine = line;
                        }
                    }
                }
            }

            LazyPosition position = element.getElementPosition();
            IntLazySavedValue xPosition = position.x();

            if (xSnapLine == null) {
                xPosition.set(x - embeddedScreen.leftPos);
            } else {
                xPosition.set(snappedX - embeddedScreen.leftPos);
            }

            IntLazySavedValue yPosition = position.y();

            if (ySnapLine == null) {
                yPosition.set(y - embeddedScreen.topPos);
            } else {
                yPosition.set(snappedY - embeddedScreen.topPos);
            }

            tryUpdateSlotPositions();
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        embeddedScreen.extractRenderState(gui, mx, my, partialTicks);

        IEditUiElement hoveringElement = getHoveringElement(mx, my);

        if (hoveringElement != null && hoveringElement.isActive()) {
            gui.requestCursor(CursorTypes.POINTING_HAND);
            extractHighlight(gui, hoveringElement, mx, my);
        }

        if (element != null && element.isActive()) {
            extractHighlight(gui, element, mx, my);

            LazyPosition position = element.getElementPosition();
            int minX = position.x().get();
            int minY = position.y().get();
            int maxX = minX + element.getWidth();
            int maxY = minY + element.getHeight();

            if (element.isExtensionRelative()) {
                LazyPosition extensionPosition = screenExtension.getElementPosition();
                int extensionX = extensionPosition.x().get();
                int extensionY = extensionPosition.y().get();
                minX += extensionX;
                minY += extensionY;
                maxX += extensionX;
                maxY += extensionY;
            }

            gui.outline(minX + embeddedScreen.leftPos, minY + embeddedScreen.topPos, maxX - minX, maxY - minY, 0xdd8888ff);

            if (shouldShowLines) {
                minX += embeddedScreen.leftPos;
                minY += embeddedScreen.topPos;
                maxX += embeddedScreen.leftPos;
                maxY += embeddedScreen.topPos;
                int midX = (minX + maxX) / 2;
                int midY = (minY + maxY) / 2;
                int pixelDistance;

                // Left
                if (minX < embeddedScreen.leftPos || midY < embeddedScreen.topPos || midY > embeddedScreen.topPos + embeddedScreen.imageHeight) {
                    pixelDistance = minX;
                } else {
                    if (minX < embeddedScreen.leftPos + embeddedScreen.imageWidth) {
                        pixelDistance = minX - embeddedScreen.leftPos;
                    } else {
                        pixelDistance = minX - embeddedScreen.leftPos - embeddedScreen.imageWidth;
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
                if (minY < embeddedScreen.topPos || midX < embeddedScreen.leftPos || midX > embeddedScreen.leftPos + embeddedScreen.imageWidth) {
                    pixelDistance = minY;
                } else {
                    if (minY < embeddedScreen.topPos + embeddedScreen.imageHeight) {
                        pixelDistance = minY - embeddedScreen.topPos;
                    } else {
                        pixelDistance = minY - embeddedScreen.topPos - embeddedScreen.imageHeight;
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
                if (maxX > embeddedScreen.leftPos + embeddedScreen.imageWidth || midY < embeddedScreen.topPos || midY > embeddedScreen.topPos + embeddedScreen.imageHeight) {
                    pixelDistance = embeddedScreen.width - maxX;
                } else {
                    if (maxX < embeddedScreen.leftPos) {
                        pixelDistance = embeddedScreen.leftPos - maxX;
                    } else {
                        pixelDistance = embeddedScreen.leftPos + embeddedScreen.imageWidth - maxX;
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
                if (maxY > embeddedScreen.topPos + embeddedScreen.imageHeight || midX < embeddedScreen.leftPos || midX > embeddedScreen.leftPos + embeddedScreen.imageWidth) {
                    pixelDistance = embeddedScreen.height - maxY;
                } else {
                    if (maxY < embeddedScreen.topPos) {
                        pixelDistance = embeddedScreen.topPos - maxY;
                    } else {
                        pixelDistance = embeddedScreen.topPos + embeddedScreen.imageHeight - maxY;
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
            extractSnapLine(gui, xSnapLine);
            extractSnapLine(gui, ySnapLine);
        }
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (OhmegaBinds.OPEN_EDIT_UI.matches(event)) {
            onClose();
            return true;
        }

        if (event.hasControlDown()) {
            if (OhmegaBinds.EDIT_REDO.matches(event) && updateHistory(redoDequeue.poll(), undoDequeue)) {
                return true;
            }

            if (OhmegaBinds.EDIT_UNDO.matches(event) && updateHistory(undoDequeue.poll(), redoDequeue)) {
                return true;
            }
        }

        if (element != null) {
            if (OhmegaBinds.EDIT_MAGNETICS.matches(event)) {
                shouldUseMagnetics = true;
            }

            if (OhmegaBinds.EDIT_NUDGE_LEFT.matches(event) && nudge(false, false)) {
                return true;
            }

            if (OhmegaBinds.EDIT_NUDGE_UP.matches(event) && nudge(true, false)) {
                return true;
            }

            if (OhmegaBinds.EDIT_NUDGE_RIGHT.matches(event) && nudge(false, true)) {
                return true;
            }

            if (OhmegaBinds.EDIT_NUDGE_DOWN.matches(event) && nudge(true, true)) {
                return true;
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

        Iterator<IEditUiElement> iterator = mutatedElements.iterator();

        while (iterator.hasNext()) {
            LazyPosition position = iterator.next().getElementPosition();

            position.x().serialise(false);
            position.y().serialise(!iterator.hasNext());
        }

        if (previousTab != null && embeddedScreen instanceof CreativeModeInventoryScreen screen) {
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
        allowSetScreen = false;
        Window window = minecraft.getWindow();

        embeddedScreen.init(window.getGuiScaledWidth(), window.getGuiScaledHeight());

        allowSetScreen = true;

        if (embeddedScreen instanceof CreativeModeInventoryScreen screen) {
            previousTab = CreativeModeInventoryScreen.selectedTab;

            for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
                if (tab.getType() == CreativeModeTab.Type.INVENTORY) {
                    screen.selectTab(tab);
                    break;
                }
            }
        }

        if (element != screenExtension) {
            element = null;
            isElementHeld = false;
        }
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        super.extractBackground(gui, mx, my, partialTicks);

        int xSteps = Math.ceilDiv(width, 256);
        int ySteps = Math.ceilDiv(height, 256);

        for (int i = 0; i < xSteps; i++) {
            for (int j = 0; j < ySteps; j++) {
                gui.blit(
                        RenderPipelines.GUI_TEXTURED,
                        BACKGROUND_LOCATION,
                        i * 256,
                        j * 256,
                        256 - (Util.getMillis() - startMillis) / 64f % 256,
                        0,
                        256,
                        256,
                        256,
                        256,
                        ARGB.white(OhmegaConfig.Client.getData().backgroundAlpha().get()));
            }
        }

        allowDimBackground = false;
        embeddedScreen.extractBackground(gui, mx, my, partialTicks);
        allowDimBackground = true;
    }

    @Override
    public void extractTransparentBackground(@NonNull GuiGraphicsExtractor gui) {
        embeddedScreen.extractTransparentBackground(gui);
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public @NonNull Screen getEmbeddedScreen() {
        return embeddedScreen;
    }

    @Override
    public boolean shouldAllowSetScreen() {
        return allowSetScreen;
    }

    @Override
    public boolean allowDimBackground() {
        return allowDimBackground;
    }
}
