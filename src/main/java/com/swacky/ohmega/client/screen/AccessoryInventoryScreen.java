package com.swacky.ohmega.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.AccessoryType;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class AccessoryInventoryScreen extends EffectRenderingInventoryScreen<AccessoryInventoryMenu> {
    protected static final ResourceLocation ACCESSORY_LOC = new ResourceLocation(Ohmega.MODID, "textures/gui/container/accessory_inventory.png");
    protected float oldMouseX;
    protected float oldMouseY;
    protected final Inventory inv;
    public AccessoryInventoryScreen(AccessoryInventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.inv = inv;
    }

    @Override
    protected void init() {
        this.renderables.clear();
        super.init();
    }

    @Override
    public void render(@Nonnull PoseStack stack, int mx, int my, float partialTicks) {
        super.render(stack, mx, my, partialTicks);
        this.oldMouseX = (float) mx;
        this.oldMouseY = (float) my;
        this.renderTooltip(stack, mx, my);
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, float pPartialTick, int pMouseX, int pMouseY) {
        if (this.minecraft != null && this.minecraft.player != null) {
            renderBackground(stack);
            ScreenHelper.set(ACCESSORY_LOC);

            // Main inventory
            blit(stack, this.leftPos, this.topPos, 0, 0, 175, 165);

            // Accessory Inventory
            blit(stack, this.leftPos + 178, this.topPos + 20, 178, 37, 26, 116);

            // Entity rendering
            InventoryScreen.renderEntityInInventoryFollowsMouse(stack, this.leftPos + 51, this.topPos + 75, 30, (float) (this.leftPos + 51) - this.oldMouseX, (float) (this.topPos + 75 - 50) - this.oldMouseY, this.minecraft.player);
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack stack, int mx, int my) {
        if (this.minecraft != null) {
            this.minecraft.font.draw(stack, MutableComponent.create(new TranslatableContents("container.crafting", null, TranslatableContents.NO_ARGS)), 97, 6, 4210752);
        }
    }

    @Override
    protected void renderTooltip(@NotNull PoseStack stack, int mx, int my) {
        if(this.menu.getCarried().isEmpty() && this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            // Normal
            for(int i = 0; i < 3; i++) {
                ScreenHelper.runIfBetween(leftPos + 182, leftPos + 199, topPos + 24 + i * 18, topPos + 41 + i * 18, mx, my, () -> this.renderTooltip(stack, MutableComponent.create(AccessoryType.NORMAL.getTranslation()), mx, my));
            }


            // Utility
            for(int i = 0; i < 2; i++) {
                ScreenHelper.runIfBetween(leftPos + 182, leftPos + 199, topPos + 78 + i * 18, topPos + 95 + i * 18, mx, my, () -> this.renderTooltip(stack, MutableComponent.create(AccessoryType.UTILITY.getTranslation()), mx, my));
            }

            // Special
            ScreenHelper.runIfBetween(leftPos + 182, leftPos + 199, topPos + 114, topPos + 131, mx, my, () -> this.renderTooltip(stack, MutableComponent.create(AccessoryType.SPECIAL.getTranslation()), mx, my));
        } else super.renderTooltip(stack, mx, my);
    }
}
