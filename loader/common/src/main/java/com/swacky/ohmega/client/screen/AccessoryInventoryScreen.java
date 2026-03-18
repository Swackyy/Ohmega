package com.swacky.ohmega.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.inv.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public final class AccessoryInventoryScreen extends EffectRenderingInventoryScreen<@NonNull AccessoryInventoryMenu> {
    private final int extraWidth;
    private final int mostSlotsPerColumn;

    @SuppressWarnings("unused")
    public AccessoryInventoryScreen(AccessoryInventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, new TranslatableComponent("container.crafting"));
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
    public void render(@NonNull PoseStack stack, int mx, int my, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mx, my, partialTicks);
        renderTooltip(stack, mx, my);
    }

    private void renderAccInv(PoseStack stack) {
        int x;

        if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            x = leftPos;
        } else {
            // Default, 2px buffer from inv, 1px to align
            x = leftPos + 175 + 2 + 1;
        }

        int lastColumnSlots = menu.slotsAvailable % mostSlotsPerColumn == 0 ? mostSlotsPerColumn : menu.slotsAvailable % mostSlotsPerColumn;
        int index = 0;

        RenderSystem.setShaderTexture(0, OhmegaCommon.ACCESSORY_LOCATION);

        for (int i = 0; i < menu.renderColumns; i++) {
            // Slots
            int slotsCreatedCurrentColumn = 0;
            for (int j = 0; true; j++) {
                blit(stack, x + 4 + 18 * i, topPos + 24 + j * 18, 4, 4, 18, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
                index++;

                if (++slotsCreatedCurrentColumn >= mostSlotsPerColumn || index >= menu.slotsAvailable) {
                    break;
                }
            }

            // Top border
            blit(stack, x + 4 + 18 * i, topPos + 20, 4, 0, 18, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

            // Bottom border
            if (i >= menu.renderColumns - 1 && lastColumnSlots != mostSlotsPerColumn) {
                blit(stack, x + 4 + 18 * i, topPos + 24 + 18 * lastColumnSlots, 4, 22, 18, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            } else {
                blit(stack, x + 4 + 18 * i, topPos + 24 + 18 * mostSlotsPerColumn, 4, 22, 18, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            }
        }

        // Side borders
        for (int i = 0; i < mostSlotsPerColumn; i++) {
            // Left
            blit(stack, x, topPos + 24 + 18 * i, 0, 4, 4, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

            // Right
            if (i >= lastColumnSlots) {
                blit(stack, x + 4 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * i, 22, 4, 4, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            } else {
                blit(stack, x + 4 + 18 * menu.renderColumns, topPos + 24 + 18 * i, 22, 4, 4, 18, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
            }
        }

        // Top left corner
        blit(stack, x, topPos + 20, 0, 0, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

        // Top right corner
        blit(stack, x + 4 + 18 * menu.renderColumns, topPos + 20, 22, 0, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

        // Bottom left corner
        blit(stack, x, topPos + 24 + 18 * mostSlotsPerColumn, 0, 22, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);

        // Bottom right corner
        blit(stack, x + 4 + 18 * menu.renderColumns, topPos + 24 + 18 * lastColumnSlots, 22, 22, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
        if (lastColumnSlots != mostSlotsPerColumn) {
            blit(stack, x + 4 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * mostSlotsPerColumn, 22, 22, 4, 4, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
        }

        // Intersecting corner
        if (lastColumnSlots != mostSlotsPerColumn) {
            blit(stack, x + 5 + 18 * (menu.renderColumns - 1), topPos + 24 + 18 * lastColumnSlots, 20, 26, 3, 3, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
        }
    }

    @Override
    protected void renderBg(@NonNull PoseStack stack, float partialTicks, int mx, int my) {
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
                RenderSystem.setShaderTexture(0, InventoryScreen.INVENTORY_LOCATION);
                blit(stack, x, topPos, 0, 0, 176, 166, 256, 256);

                // Accessory Inventory
                renderAccInv(stack);

                // Entity rendering
                InventoryScreen.renderEntityInInventory(x + 51, topPos + 75, 30, x + 51 - mx, topPos + 25 - my, player);
            }
        }
    }

    @Override
    protected void renderTooltip(@NonNull PoseStack stack, int mx, int my) {
        if (menu.getCarried().isEmpty() && hoveredSlot instanceof AccessorySlot accSlot && accSlot.getType().displayHoverText() && OhmegaConfig.Client.showHoverTooltip() && !hoveredSlot.hasItem()) {
            renderTooltip(stack, accSlot.getType().getTranslation(), mx, my);
        } else {
            super.renderTooltip(stack, mx, my);
        }
    }

    @Override
    protected void renderLabels(PoseStack stack, int mx, int my) {
        font.draw(stack, title, titleLabelX, titleLabelY, 4210752);
    }

    @Override
    protected boolean hasClickedOutside(double x, double y, int left, int top, int button) {
        if (super.hasClickedOutside(x, y, left, top, button)) {
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
