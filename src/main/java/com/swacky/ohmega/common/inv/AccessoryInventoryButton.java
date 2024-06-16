package com.swacky.ohmega.common.inv;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.C2S.OpenAccessoryGuiPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AccessoryInventoryButton extends AbstractButton {
    private static final ResourceLocation LOC = new ResourceLocation(Ohmega.MODID, "textures/gui/accessory_button.png");
    protected final Minecraft mc;
    protected final int xStart;
    protected final int yStart;
    protected final int yOffsHovered;
    public AccessoryInventoryButton(AbstractContainerScreen<?> screen, int xStart, int yStart, int xOffs, int yOffs, int yOffsHovered) {
        super(screen.getGuiLeft() + xOffs, screen.height / 2 - yOffs, 20, 18, MutableComponent.create(new LiteralContents("")));
        this.mc = screen.getMinecraft();
        this.xStart = xStart;
        this.yStart = yStart;
        this.yOffsHovered = yOffsHovered;
    }

    @Override
    public void onPress() {
        if (mc.gameMode != null && mc.player != null) {
            if (mc.player.containerMenu instanceof AccessoryInventoryMenu) {
                ModNetworking.sendToServer(new OpenInventoryPacket());
                mc.setScreen(new InventoryScreen(mc.player));
            } else {
                ModNetworking.sendToServer(new OpenAccessoryGuiPacket(mc.player.getId()));
            }
        }
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, LOC);
        int offsY = this.yStart;
        if (this.isHoveredOrFocused()) {
            offsY += this.yOffsHovered;
        }

        RenderSystem.enableDepthTest();
        blit(stack, this.x, this.y, (float)this.xStart, (float)offsY, this.width, this.height, 256, 256);
    }
}
