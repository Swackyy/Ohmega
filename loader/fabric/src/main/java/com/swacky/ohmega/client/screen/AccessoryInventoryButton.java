package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.common.OhmegaCommon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jetbrains.annotations.NotNull;

public class AccessoryInventoryButton extends AbstractButton {
    protected final Minecraft mc;
    private final AbstractContainerScreen<?> screen;
    protected final OhmegaConfig.ButtonStyle style;
    public AccessoryInventoryButton(OhmegaConfig.ButtonStyle style, AbstractContainerScreen<?> screen) {
        super(getXAdjusted(screen, style), screen.topPos + style.getY(), style.getWidth(), style.getHeight(), MutableComponent.create(PlainTextContents.EMPTY));
        this.mc = screen.minecraft;
        this.screen = screen;
        this.style = style;
    }

    private static int getXAdjusted(AbstractContainerScreen<?> screen, OhmegaConfig.ButtonStyle style) {
        if (screen instanceof AccessoryInventoryScreen accScreen && OhmegaConfig.CONFIG_CLIENT.side.get() == OhmegaConfig.Side.LEFT) {
            return screen.leftPos + style.getX() + accScreen.getExtraWidth();
        }

        return screen.leftPos + style.getX();
    }

    private boolean isVisible() {
        return this.visible && (this.screen instanceof AccessoryInventoryScreen || (this.screen instanceof InventoryScreen inventoryScreen && !inventoryScreen.recipeBookComponent.isVisible()));
    }

    private void fixPos() {
        this.setX(getXAdjusted(this.screen, this.style));
        this.setY(this.screen.topPos + this.style.getY());
    }

    @Override
    protected boolean isValidClickButton(@NotNull MouseButtonInfo info) {
        return this.isVisible() && super.isValidClickButton(info);
    }

    @Override
    public void onPress(@NotNull InputWithModifiers input) {
        if (mc.player != null) {
            if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                if (mc.screen instanceof AccessoryInventoryScreen) {
                    mc.player.containerMenu = mc.player.inventoryMenu;
                    mc.setScreen(new InventoryScreen(mc.player));
                    ClientPlayNetworking.send(new OpenInventoryPacket());
                } else {
                    ClientPlayNetworking.send(new OpenAccessoryInventoryPacket());
                }
            } else {
                mc.player.containerMenu = mc.player.inventoryMenu;
                mc.setScreen(new InventoryScreen(mc.player));
                ClientPlayNetworking.send(new OpenInventoryPacket());
            }
        }
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics gui, int mx, int my, float partialTicks) {
        if (this.isVisible()) {
            this.fixPos();
            int hoveredOffsX;
            int hoveredOffsY;
            if (this.isHoveredOrFocused()) {
                if (this.style.shouldUseWidthHovered()) {
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

            gui.blit(RenderPipelines.GUI_TEXTURED, OhmegaCommon.ACCESSORY_LOCATION, this.getX(), this.getY(), (float) this.style.getUOffs() + hoveredOffsX, (float) this.style.getVOffs() + hoveredOffsY, this.width, this.height, 26, 71);
        }
    }
}
