package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.inv.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AccessoryInventoryScreen extends AbstractContainerScreen<AccessoryInventoryMenu> {
    protected final Inventory inv;
    protected final EffectsInInventory effects;
    private final int extraWidth;

    @SuppressWarnings("unused")
    public AccessoryInventoryScreen(AccessoryInventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, Component.translatable("container.crafting"));
        this.inv = inv;
        this.effects = new EffectsInInventory(this);
        this.titleLabelX = 97;
        this.extraWidth = 2 + 4 * 2 + 18 * (int) Math.min(Math.ceil((double) AccessoryHelper.getSlotTypes().size() / Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get(), OhmegaConfig.CONFIG_CLIENT.maxColumnSlots.get())), OhmegaConfig.CONFIG_CLIENT.maxColumns.get());

        if (OhmegaConfig.CONFIG_CLIENT.side.get() == OhmegaConfig.Side.RIGHT) {
            this.imageWidth += extraWidth;
        }
    }

    @Override
    protected void init() {
        this.renderables.clear();

        AccessoryInventoryButton button = new AccessoryInventoryButton(OhmegaConfig.CONFIG_CLIENT.buttonStyle.get(), this);
        addRenderableWidget(button);

        if (OhmegaConfig.CONFIG_CLIENT.side.get() == OhmegaConfig.Side.LEFT) {
            this.leftPos = (this.width - this.imageWidth) / 2;
        } else {
            this.leftPos = (this.width - this.imageWidth + extraWidth) / 2;
        }

        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int mx, int my, float partialTicks) {
        super.render(gui, mx, my, partialTicks);
        this.effects.renderEffects(gui, mx, my);
        this.renderTooltip(gui, mx, my);
    }

    @Override
    public boolean showsActiveEffects() {
        return this.effects.canSeeEffects();
    }

    protected void renderAccInv(GuiGraphics gui) {
        final int renderSlots = Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnSlots.get(), OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get());
        final int renderColumns = (int) Math.min(Math.ceil((double) AccessoryHelper.getSlotTypes().size() / renderSlots), OhmegaConfig.CONFIG_CLIENT.maxColumns.get());
        final int slotsAvailable = Math.min(renderColumns * renderSlots, AccessoryHelper.getSlotTypes().size());
        final int mostSlotsPerColumn = Math.min(renderSlots, AccessoryHelper.getSlotTypes().size());

        final int x;
        if (OhmegaConfig.CONFIG_CLIENT.side.get() == OhmegaConfig.Side.LEFT) {
            // Default, 2px buffer from inv, 4 px buffer on both sides, slots columns width, 1px to align
            x = this.leftPos - 2 - 4 * 2 - 18 * renderColumns;
        } else {
            // Default, 2px buffer from inv, 1px to align
            x = this.leftPos + 175 + 2 + 1;
        }

        final int lastColumnSlots = slotsAvailable % mostSlotsPerColumn == 0 ? mostSlotsPerColumn : slotsAvailable % mostSlotsPerColumn;

        int index = 0;
        for (int i = 0; i < renderColumns; i++) {
            // Slots
            int slotsCreatedCurrentColumn = 0;
            for (int j = 0; true; j++) {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, this.topPos + 24 + j * 18, 4, 4, 18, 18, 26, 71);
                index++;
                slotsCreatedCurrentColumn++;
                if (slotsCreatedCurrentColumn >= mostSlotsPerColumn || index >= slotsAvailable) {
                    break;
                }
            }

            // Top border
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, this.topPos + 20, 4, 0, 18, 4, 26, 71);

            // Bottom border
            if (i >= renderColumns - 1 && lastColumnSlots != mostSlotsPerColumn) {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, this.topPos + 24 + 18 * lastColumnSlots, 4, 22, 18, 4, 26, 71);
            } else {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, this.topPos + 24 + 18 * mostSlotsPerColumn, 4, 22, 18, 4, 26, 71);
            }
        }

        // Side borders
        for (int i = 0; i < mostSlotsPerColumn; i++) {
            // Left
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x, this.topPos + 24 + 18 * i, 0, 4, 4, 18, 26, 71);

            // Right
            if (i >= lastColumnSlots) {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * (renderColumns - 1), this.topPos + 24 + 18 * i, 22, 4, 4, 18, 26, 71);
            } else {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * renderColumns, this.topPos + 24 + 18 * i, 22, 4, 4, 18, 26, 71);
            }
        }

        // Top left corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x, this.topPos + 20, 0, 0, 4, 4, 26, 71);

        // Top right corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * renderColumns, this.topPos + 20, 22, 0, 4, 4, 26, 71);

        // Bottom left corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x, this.topPos + 24 + 18 * mostSlotsPerColumn, 0, 22, 4, 4, 26, 71);

        // Bottom right corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * renderColumns, this.topPos + 24 + 18 * lastColumnSlots, 22, 22, 4, 4, 26, 71);
        if (lastColumnSlots != mostSlotsPerColumn) {
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * (renderColumns - 1), this.topPos + 24 + 18 * mostSlotsPerColumn, 22, 22, 4, 4, 26, 71);
        }

        // Intersecting corner
        if (lastColumnSlots != mostSlotsPerColumn) {
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 5 + 18 * (renderColumns - 1), this.topPos + 24 + 18 * lastColumnSlots, 20, 26, 3, 3, 26, 71);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics gui, float partialTicks, int mx, int my) {
        if (this.minecraft != null && this.minecraft.player != null) {
            // Main inventory
            gui.blit(RenderPipelines.GUI_TEXTURED, InventoryScreen.INVENTORY_LOCATION, this.leftPos, this.topPos, 0, 0, 176, 166, 256, 256);

            // Accessory Inventory
            this.renderAccInv(gui);

            // Entity rendering
            InventoryScreen.renderEntityInInventoryFollowsMouse(gui, this.leftPos + 26, this.topPos + 8, this.leftPos + 75, this.topPos + 78, 30, 0.0625f, mx, my, this.minecraft.player);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics gui, int mx, int my) {
        gui.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics gui, int mx, int my) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof AccessorySlot accSlot && accSlot.getType().displayHoverText() && OhmegaConfig.CONFIG_CLIENT.tooltip.get() && !this.hoveredSlot.hasItem() && this.minecraft != null) {
            gui.setTooltipForNextFrame(accSlot.getType().getTranslation(), mx, my);
        } else {
            super.renderTooltip(gui, mx, my);
        }
    }
}
