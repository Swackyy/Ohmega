package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.ModNetworking;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jetbrains.annotations.NotNull;

public class AccessoryInventoryButton extends AbstractButton {
    protected final Minecraft mc;
    private final AbstractContainerScreen<?> screen;
    protected final int x;
    protected final int y;
    protected final int uOffs;
    protected final int vOffs;
    protected final boolean shouldUseWidthHovered;
    public AccessoryInventoryButton(OhmegaConfig.ButtonStyle style, AbstractContainerScreen<?> screen) {
        super(screen.getGuiLeft() + style.getX(), screen.getGuiTop() + style.getY(), style.getWidth(), style.getHeight(), MutableComponent.create(PlainTextContents.EMPTY));
        this.mc = screen.getMinecraft();
        this.screen = screen;
        this.x = style.getX();
        this.y = style.getY();
        this.uOffs = style.getUOffs();
        this.vOffs = style.getVOffs();
        this.shouldUseWidthHovered = style.shouldUseWidthHovered();
    }

    @Override
    public void onPress() {
        if (mc.player != null) {
            if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                if (mc.screen instanceof AccessoryInventoryScreen) {
                    ModNetworking.sendToServer(OpenInventoryPacket.INSTANCE);
                    mc.setScreen(new InventoryScreen(mc.player));
                } else {
                    ModNetworking.sendToServer(OpenAccessoryInventoryPacket.INSTANCE);
                }
            } else {
                ModNetworking.sendToServer(OpenInventoryPacket.INSTANCE);
            }
        }
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    private void fixPos() {
        this.setX(this.screen.getGuiLeft() + this.x);
        this.setY(this.screen.getGuiTop() + this.y);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        this.fixPos();
        int hoveredOffsX;
        int hoveredOffsY;
        if (this.isHoveredOrFocused()) {
            if (this.shouldUseWidthHovered) {
                hoveredOffsX = this.width;
                hoveredOffsY = 0;
            } else {
                hoveredOffsX = 0;
                hoveredOffsY = this.height;
            }
        } else {
            hoveredOffsX = 0;
            hoveredOffsY = 0;
        }

        gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, this.getX(), this.getY(), (float)this.uOffs + hoveredOffsX, (float)this.vOffs + hoveredOffsY, this.width, this.height, 26, 71);
    }
}
