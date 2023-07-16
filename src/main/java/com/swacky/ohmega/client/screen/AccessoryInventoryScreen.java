package com.swacky.ohmega.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class AccessoryInventoryScreen extends EffectRenderingInventoryScreen<AccessoryInventoryMenu> { // SURVIVAL INV
    protected ResourceLocation ACCESSORY_LOC = new ResourceLocation(Ohmega.MODID, "textures/gui/container/accessory_inventory.png");
    protected float oldMouseX;
    protected float oldMouseY;
    private final Inventory inv;
    public AccessoryInventoryScreen(AccessoryInventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.inv = inv;
    }

    @Override
    protected void init() {
        this.renderables.clear();
        super.init();
        if (inv.player instanceof AbstractClientPlayer player) {
            this.leftPos = (this.width - this.imageWidth) / 2;
        }
    }

    @Override
    public void containerTick() {
        this.canSeeEffects();
        this.leftPos = (this.width - this.imageWidth) / 2;
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, float pPartialTick, int pMouseX, int pMouseY) {
        if (this.minecraft != null && this.minecraft.player != null) {
            renderBackground(stack);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, ACCESSORY_LOC);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            // Main inventory
            this.blit(stack, this.leftPos, this.topPos, 0, 0, 175, 165);

            // Accessory Inventory
            this.blit(stack, this.leftPos + 178, this.topPos + 20, 178, 37, 26, 116);
            InventoryScreen.renderEntityInInventory(this.leftPos + 51, this.topPos + 75, 30, (float) (this.leftPos + 51) - this.oldMouseX, (float) (this.topPos + 75 - 50) - this.oldMouseY, this.minecraft.player);
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack stack, int pMouseX, int pMouseY) {
        if (this.minecraft != null) {
            this.minecraft.font.draw(stack, new TranslatableComponent("container.crafting"), 97, 6, 4210752);
        }
    }
}
