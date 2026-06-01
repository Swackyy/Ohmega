package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.client.screen.widget.ToggleVisibilityButton;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Ohmega's default accessory screen extension implementation
 */
public final class DefaultScreenExtension extends AccessoryScreenExtension implements IEntityRenderingExtension {
    private static final Identifier SLOT_LOCATION = Ohmega.id("textures/gui/container/accessory_inventory/slot.png");

    private final int renderColumns;
    private final int slotsAvailable;
    private final int mostSlotsPerColumn;
    private final int lastColumnSlots;

    private boolean flipEntity = false;

    @SuppressWarnings("unused")
    public DefaultScreenExtension(AbstractContainerScreen<?> screen, AccessoryMenuExtension menuExtension) {
        super(screen, menuExtension);

        int size = AccessoryHelper.getSlotTypes().size();
        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();
        int maxRenderSlots = data.maxColumnRenderSlots().get();
        int maxColumnSlots = data.maxColumnSlots().get();
        int maxColumns = data.maxColumns().get();
        int renderSlots = Math.min(maxColumnSlots, maxRenderSlots);
        this.renderColumns = (int) Math.min(Math.ceil((double) size / renderSlots), maxColumns);
        // 2px buffer, 2 * 4px extension borders, 18 * number of columns
        this.slotsAvailable = Math.min(renderColumns * renderSlots, size);
        this.mostSlotsPerColumn = Math.min(renderSlots, size);
        this.lastColumnSlots = slotsAvailable % mostSlotsPerColumn == 0 ? mostSlotsPerColumn : slotsAvailable % mostSlotsPerColumn;
    }

    @Override
    public void initExtension(WidgetAdder adder) {
        if (OhmegaConfig.Server.getData().allowHideAccessories().get()) {
            int index = 0;

            for (int i = 0; i < renderColumns; i++) {
                int addedCurrentColumn = 0;

                for (int j = 0; true; j++) {
                    adder.addOverlay(new ToggleVisibilityButton(getScreen(), 1 + 18 * (i + 1), 2 + j * 18, getMenuExtension().getOwner(), index++));

                    if (++addedCurrentColumn >= mostSlotsPerColumn || index >= slotsAvailable) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public @NonNull List<Rect2i> getRects() {
        // todo: check for fill direction
        return List.of(
                new Rect2i(
                        0,
                        0,
                        (renderColumns - 1) * 18 + 4,
                        mostSlotsPerColumn * 18 + 8),
                new Rect2i(
                        (renderColumns - 1) * 18 + 4,
                        0,
                        22,
                        lastColumnSlots * 18 + 8));
    }

    @Override
    public int getWidth() {
        return 8 + renderColumns * 18;
    }

    @Override
    public int getHeight() {
        return 8 + mostSlotsPerColumn * 18;
    }

    private void blit(GuiGraphicsExtractor gui, int x, int y, int u, int v, int width, int height) {
        gui.blit(RenderPipelines.GUI_TEXTURED, SLOT_LOCATION, x, y, u, v, width, height, 26, 26);
    }

    @Override
    public void extractExtension(GuiGraphicsExtractor gui) {
        int index = 0;

        for (int i = 0; i < renderColumns; i++) {
            // Slots
            int slotsCreatedCurrentColumn = 0;

            for (int j = 0; true; j++) {
                blit(gui, 4 + 18 * i, 4 + j * 18, 4, 4, 18, 18);

                index++;

                if (++slotsCreatedCurrentColumn >= mostSlotsPerColumn || index >= slotsAvailable) {
                    break;
                }
            }

            // Top border
            blit(gui, 4 + 18 * i, 0, 4, 0, 18, 4);

            // Bottom border
            if (i >= renderColumns - 1 && lastColumnSlots != mostSlotsPerColumn) {
                blit(gui, 4 + 18 * i, 4 + 18 * lastColumnSlots, 4, 22, 18, 4);
            } else {
                blit(gui, 4 + 18 * i, 4 + 18 * mostSlotsPerColumn, 4, 22, 18, 4);
            }
        }

        // Side borders
        for (int i = 0; i < mostSlotsPerColumn; i++) {
            // Left
            blit(gui, 0, 4 + 18 * i, 0, 4, 4, 18);

            // Right
            if (i >= lastColumnSlots) {
                blit(gui, 4 + 18 * (renderColumns - 1), 4 + 18 * i, 22, 4, 4, 18);
            } else {
                blit(gui, 4 + 18 * renderColumns, 4 + 18 * i, 22, 4, 4, 18);
            }
        }

        // Top left corner
        blit(gui, 0, 0, 0, 0, 4, 4);

        // Top right corner
        blit(gui, 4 + 18 * renderColumns, 0, 22, 0, 4, 4);

        // Bottom left corner
        blit(gui, 0, 4 + 18 * mostSlotsPerColumn, 0, 22, 4, 4);

        // Bottom right corner
        blit(gui, 4 + 18 * renderColumns, 4 + 18 * lastColumnSlots, 22, 22, 4, 4);

        if (lastColumnSlots != mostSlotsPerColumn) {
            blit(gui, 4 + 18 * (renderColumns - 1), 4 + 18 * mostSlotsPerColumn, 22, 22, 4, 4);
        }

        // Intersecting corner
        if (lastColumnSlots != mostSlotsPerColumn) {
            blit(gui, 5 + 18 * (renderColumns - 1), 4 + 18 * lastColumnSlots, 19, 22, 3, 3);
            blit(gui, 5 + 18 * (renderColumns - 1), 4 + 18 * lastColumnSlots + 1, 21, 22, 1, 1);
        }
    }

    @Override
    public boolean isEntityFlipped() {
        return flipEntity;
    }

    @Override
    public void setFlipEntity(boolean value) {
        flipEntity = value;
    }

    @Override
    public boolean hasClickedOutside(double mx, double my) {
        if (OhmegaConfig.Client.getData().fillDirection().getObject() == OhmegaConfig.Client.Service.FillDirection.LEFT) {
            // todo
        } else {
            // Left border
            if (mx < 0) {
                return true;
            }

            // Right border
            if (mx > 8 + renderColumns * 18) {
                return true;
            }

            // Top border
            if (my < 0) {
                return true;
            }

            // Bottom border
            if (my > 8 + mostSlotsPerColumn * 18) {
                return true;
            }

            // Bottom border for an incomplete last column
            if (lastColumnSlots != mostSlotsPerColumn) {
                return mx > 4 + 18 * (renderColumns - 1) && my > 8 + lastColumnSlots * 18;
            }
        }

        return false;
    }
}
