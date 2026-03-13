package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class AccessoryInventoryButton extends AbstractButton {
    private final AbstractContainerScreen<?> screen;
    private final OhmegaConfig.Client.Service.ButtonStyle style;

    public AccessoryInventoryButton(OhmegaConfig.Client.Service.ButtonStyle style, AbstractContainerScreen<?> screen) {
        super(AccessoryInventoryButton.getXAdjusted(screen, style), screen.topPos + style.getData().y(), style.getData().width(), style.getData().height(), Component.empty());
        this.screen = screen;
        this.style = style;
    }

    private static int getXAdjusted(AbstractContainerScreen<?> screen, OhmegaConfig.Client.Service.ButtonStyle style) {
        if (screen instanceof AccessoryInventoryScreen accScreen && OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            return screen.leftPos + style.getData().x() + accScreen.getExtraWidth();
        }

        return screen.leftPos + style.getData().x();
    }

    private boolean isVisible() {
        return visible && (screen instanceof AccessoryInventoryScreen || (screen instanceof InventoryScreen inventoryScreen && !inventoryScreen.getRecipeBookComponent().isVisible()));
    }

    private void fixPos() {
        setX(getXAdjusted(screen, style));
        setY(screen.topPos + style.getData().y());
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return isVisible() && super.isValidClickButton(button);
    }

    @Override
    public void onPress() {
        Minecraft mc = screen.minecraft;

        if (mc != null) {
            LocalPlayer player = mc.player;

            if (player != null) {
                if (!player.isCreative() && !player.isSpectator()) {
                    if (screen instanceof AccessoryInventoryScreen) {
                        player.containerMenu = player.inventoryMenu;
                        mc.setScreen(new InventoryScreen(player));
                        OhmegaNetworking.C2S.send(OpenInventoryPacket.INSTANCE);
                    } else {
                        OhmegaNetworking.C2S.send(OpenAccessoryInventoryPacket.INSTANCE);
                    }
                } else {
                    player.containerMenu = player.inventoryMenu;
                    mc.setScreen(new InventoryScreen(player));
                    OhmegaNetworking.C2S.send(OpenInventoryPacket.INSTANCE);
                }
            }
        }
    }

    @Override
    public void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @Override
    public void renderWidget(@NonNull GuiGraphics gui, int mx, int my, float partialTicks) {
        if (isVisible()) {
            fixPos();

            int hoveredOffsX;
            int hoveredOffsY;

            if ((style.highlightWhenHovered() && isHoveredOrFocused()) || screen instanceof AccessoryInventoryScreen) {
                if (style.offsetWidthHighlighted()) {
                    hoveredOffsX = width;
                    hoveredOffsY = 0;
                } else {
                    hoveredOffsX = 0;
                    hoveredOffsY = height;
                }
            } else {
                hoveredOffsX = 0;
                hoveredOffsY = 0;
            }

            gui.blit(OhmegaCommon.ACCESSORY_LOCATION, this.getX(), this.getY(), (float) style.getData().u() + hoveredOffsX, (float) style.getData().v() + hoveredOffsY, width, height, OhmegaCommon.ACCESSORY_ADDON_WIDTH, OhmegaCommon.ACCESSORY_ADDON_HEIGHT);
        }
    }
}
