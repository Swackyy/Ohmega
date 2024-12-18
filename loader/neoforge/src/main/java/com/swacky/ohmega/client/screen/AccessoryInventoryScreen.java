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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AccessoryInventoryScreen extends AbstractContainerScreen<AccessoryInventoryMenu> {
    protected static final ResourceLocation VANILLA_LOC = OhmegaCommon.mcRl("textures/gui/container/inventory.png");
    protected static final ResourceLocation ACCESSORY_LOC = OhmegaCommon.rl("textures/gui/accessory_addon.png");

    private static int extraWidth;

    protected float oldMouseX;
    protected float oldMouseY;
    protected final Inventory inv;
    private final EffectsInInventory effects;

    public AccessoryInventoryScreen(AccessoryInventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.inv = inv;
        defineExtraWidth();
        if (OhmegaConfig.CONFIG_CLIENT.side.get() == OhmegaConfig.Side.RIGHT) {
            this.imageWidth += extraWidth;
        }
        this.effects = new EffectsInInventory(this);
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
        this.oldMouseX = (float) mx;
        this.oldMouseY = (float) my;
        this.renderTooltip(gui, mx, my);
    }

    @Override
    public boolean showsActiveEffects() {
        return this.effects.canSeeEffects();
    }

    public static void defineExtraWidth() {
        extraWidth = 2 + 4 * 2 + 18 * (int) Math.min(Math.ceil((double) AccessoryHelper.getSlotTypes().size() / Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get(), OhmegaConfig.CONFIG_CLIENT.maxColumnSlots.get())), OhmegaConfig.CONFIG_CLIENT.maxColumns.get());
    }

    protected void renderAccInv(GuiGraphics gui) {
        final int renderColumns = (int) Math.min(Math.ceil((double) AccessoryHelper.getSlotTypes().size() / Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get(), OhmegaConfig.CONFIG_CLIENT.maxColumnSlots.get())), OhmegaConfig.CONFIG_CLIENT.maxColumns.get());
        final int slotsAvailable = Math.min(renderColumns * Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnSlots.get(), OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get()), AccessoryHelper.getSlotTypes().size());
        final int mostSlotsPerColumn = Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get(), AccessoryHelper.getSlotTypes().size());

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
                gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * i, this.topPos + 24 + j * 18, 4, 4, 18, 18, 26, 71);
                index++;
                slotsCreatedCurrentColumn++;
                if (slotsCreatedCurrentColumn >= mostSlotsPerColumn || index >= slotsAvailable) {
                    break;
                }
            }

            // Top border
            gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * i, this.topPos + 20, 4, 0, 18, 4, 26, 71);

            // Bottom border
            if (i >= renderColumns - 1 && lastColumnSlots != mostSlotsPerColumn) {
                gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * i, this.topPos + 24 + 18 * lastColumnSlots, 4, 22, 18, 4, 26, 71);
            } else {
                gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * i, this.topPos + 24 + 18 * mostSlotsPerColumn, 4, 22, 18, 4, 26, 71);
            }
        }

        // Side borders
        for (int i = 0; i < mostSlotsPerColumn; i++) {
            // Left
            gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x, this.topPos + 24 + 18 * i, 0, 4, 4, 18, 26, 71);

            // Right
            if (i >= lastColumnSlots) {
                gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * (renderColumns - 1), this.topPos + 24 + 18 * i, 22, 4, 4, 18, 26, 71);
            } else {
                gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * renderColumns, this.topPos + 24 + 18 * i, 22, 4, 4, 18, 26, 71);
            }
        }

        // Top left corner
        gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x, this.topPos + 20, 0, 0, 4, 4, 26, 71);

        // Top right corner
        gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * renderColumns, this.topPos + 20, 22, 0, 4, 4, 26, 71);

        // Bottom left corner
        gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x, this.topPos + 24 + 18 * mostSlotsPerColumn, 0, 22, 4, 4, 26, 71);

        // Bottom right corner
        gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * renderColumns, this.topPos + 24 + 18 * lastColumnSlots, 22, 22, 4, 4, 26, 71);
        if (lastColumnSlots != mostSlotsPerColumn) {
            gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 4 + 18 * (renderColumns - 1), this.topPos + 24 + 18 * mostSlotsPerColumn, 22, 22, 4, 4, 26, 71);
        }

        // Intersecting corner
        if (lastColumnSlots != mostSlotsPerColumn) {
            gui.blit(RenderType::guiTextured, ACCESSORY_LOC, x + 5 + 18 * (renderColumns - 1), this.topPos + 24 + 18 * lastColumnSlots, 20, 26, 3, 3, 26, 71);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics gui, float partialTicks, int mx, int my) {
        if (this.minecraft != null && this.minecraft.player != null) {
            // Main inventory
            gui.blit(RenderType::guiTextured, VANILLA_LOC, this.leftPos, this.topPos, 0, 0, 176, 166, 256, 256);

            // Accessory Inventory
            this.renderAccInv(gui);

            // Entity rendering
            InventoryScreen.renderEntityInInventoryFollowsMouse(gui, this.leftPos + 26, this.topPos + 8, this.leftPos + 75, this.topPos + 78, 30, 0.0625f, mx, my, this.minecraft.player);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics gui, int mx, int my) {
        if (this.minecraft != null) {
            gui.drawString(this.minecraft.font, Component.translatable("container.crafting"), 97, 6, 4210752, false);
        }
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics gui, int mx, int my) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof AccessorySlot accSlot && accSlot.getType().displayHoverText() && OhmegaConfig.CONFIG_CLIENT.tooltip.get() && !this.hoveredSlot.hasItem() && this.minecraft != null) {
            gui.renderTooltip(this.minecraft.font, accSlot.getType().getTranslation(), mx, my);
        } else {
            super.renderTooltip(gui, mx, my);
        }
    }
}
