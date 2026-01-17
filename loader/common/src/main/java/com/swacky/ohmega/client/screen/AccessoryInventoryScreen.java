package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.inv.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public final class AccessoryInventoryScreen extends AbstractContainerScreen<@NonNull AccessoryInventoryMenu> {
    private final EffectsInInventory effects;
    private final int extraWidth;
    private final int mostSlotsPerColumn;

    @SuppressWarnings("unused")
    public AccessoryInventoryScreen(AccessoryInventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, Component.translatable("container.crafting"));
        this.effects = new EffectsInInventory(this);
        this.extraWidth = 2 + 4 * 2 + 18 * (int) Math.min(Math.ceil((double) AccessoryHelper.getSlotTypes().size() / Math.min(OhmegaConfig.Client.maxColumnRenderSlots(), OhmegaConfig.Client.maxColumnSlots())), OhmegaConfig.Client.maxColumns());
        this.imageWidth += extraWidth;

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            this.titleLabelX = 97 + extraWidth;
        } else {
            this.titleLabelX = 97;
        }

        this.mostSlotsPerColumn = Math.min(menu.renderSlots, AccessoryHelper.getSlotTypes().size());
    }

    @Override
    protected void init() {
        renderables.clear();

        AccessoryInventoryButton button = new AccessoryInventoryButton(OhmegaConfig.Client.buttonStyle(), this);

        addRenderableWidget(button);

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            leftPos = (width - imageWidth - extraWidth) / 2;
        } else {
            leftPos = (width - imageWidth + extraWidth) / 2;
        }

        topPos = (height - imageHeight) / 2;
    }

    @Override
    public void render(@NonNull GuiGraphics gui, int mx, int my, float partialTicks) {
        super.render(gui, mx, my, partialTicks);
        effects.renderEffects(gui, mx, my);
        renderTooltip(gui, mx, my);
    }

    @Override
    public boolean showsActiveEffects() {
        return effects.canSeeEffects();
    }

    private void renderAccInv(GuiGraphics gui) {
        int x;

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            x = leftPos;
        } else {
            // Default, 2px buffer from inv, 1px to align
            x = leftPos + 175 + 2 + 1;
        }

        int lastColumnSlots = menu.slotsAvailable % mostSlotsPerColumn == 0 ? mostSlotsPerColumn : menu.slotsAvailable % mostSlotsPerColumn;
        int index = 0;

        for (int i = 0; i < menu.renderColumns; i++) {
            // Slots
            int slotsCreatedCurrentColumn = 0;
            for (int j = 0; true; j++) {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, topPos + 24 + j * 18, 4, 4, 18, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
                index++;

                if (++slotsCreatedCurrentColumn >= mostSlotsPerColumn || index >= menu.slotsAvailable) {
                    break;
                }
            }

            // Top border
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, topPos + 20, 4, 0, 18, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

            // Bottom border
            if (i >= menu.renderColumns - 1 && lastColumnSlots != mostSlotsPerColumn) {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, topPos + 24 + 18 * lastColumnSlots, 4, 22, 18, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            } else {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * i, topPos + 24 + 18 * mostSlotsPerColumn, 4, 22, 18, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            }
        }

        // Side borders
        for (int i = 0; i < mostSlotsPerColumn; i++) {
            // Left
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x, topPos + 24 + 18 * i, 0, 4, 4, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

            // Right
            if (i >= lastColumnSlots) {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * i, 22, 4, 4, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            } else {
                gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * menu.renderColumns, topPos + 24 + 18 * i, 22, 4, 4, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            }
        }

        // Top left corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x, topPos + 20, 0, 0, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

        // Top right corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * menu.renderColumns, topPos + 20, 22, 0, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

        // Bottom left corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x, topPos + 24 + 18 * mostSlotsPerColumn, 0, 22, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

        // Bottom right corner
        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * menu.renderColumns, topPos + 24 + 18 * lastColumnSlots, 22, 22, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
        if (lastColumnSlots != mostSlotsPerColumn) {
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 4 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * mostSlotsPerColumn, 22, 22, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
        }

        // Intersecting corner
        if (lastColumnSlots != mostSlotsPerColumn) {
            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, x + 5 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * lastColumnSlots, 20, 26, 3, 3, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
        }
    }

    @Override
    protected void renderBg(@NonNull GuiGraphics gui, float partialTicks, int mx, int my) {
        if (minecraft != null) {
            LocalPlayer player = minecraft.player;

            if (player != null) {
                int x;

                if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
                    x = leftPos + extraWidth;
                } else {
                    x = leftPos;
                }

                // Main inventory
                gui.blit(RenderPipelines.GUI_TEXTURED, InventoryScreen.INVENTORY_LOCATION, x, topPos, 0, 0, 176, 166, 256, 256);

                // Accessory Inventory
                renderAccInv(gui);

                // Entity rendering
                InventoryScreen.renderEntityInInventoryFollowsMouse(gui, x + 26, topPos + 8, x + 75, topPos + 78, 30, 0.0625f, mx, my, player);
            }
        }
    }

    @Override
    protected void renderLabels(@NonNull GuiGraphics gui, int mx, int my) {
        gui.drawString(font, title, titleLabelX, titleLabelY, -12566464, false);
    }

    @Override
    protected void renderTooltip(@NonNull GuiGraphics gui, int mx, int my) {
        if (menu.getCarried().isEmpty() && hoveredSlot instanceof AccessorySlot accSlot && accSlot.getType().displayHoverText() && OhmegaConfig.Client.showHoverTooltip() && !hoveredSlot.hasItem()) {
            gui.setTooltipForNextFrame(accSlot.getType().getTranslation(), mx, my);
        } else {
            super.renderTooltip(gui, mx, my);
        }
    }

    @Override
    protected boolean hasClickedOutside(double x, double y, int left, int top) {
        if (super.hasClickedOutside(x, y, left, top)) {
            return true;
        }

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            // Ensure on correct side
            if (x < leftPos + extraWidth) {
                // Above
                if (y > topPos && y < topPos + 20) {
                    return true;
                }

                // Below
                return y > topPos + 20 + 4 + mostSlotsPerColumn * 18 + 4;
            }
        } else {
            // Ensure on correct side
            if (x > leftPos + 175) {
                // Above
                if (y > topPos && y < topPos + 20) {
                    return true;
                }

                // Below
                return y > topPos + 20 + 4 + mostSlotsPerColumn * 18 + 4;
            }
        }

        return false;
    }

    public int getExtraWidth() {
        return extraWidth;
    }
}
