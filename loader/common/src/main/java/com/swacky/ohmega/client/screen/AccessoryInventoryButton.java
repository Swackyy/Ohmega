package com.swacky.ohmega.client.screen;

import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class AccessoryInventoryButton extends AbstractButton {
    private final AbstractContainerScreen<?> screen;
    private final OhmegaConfig.Client.Service.ButtonStyle style;

    public AccessoryInventoryButton(AbstractContainerScreen<?> screen, OhmegaConfig.Client.Service.ButtonStyle style) {
        super(AccessoryInventoryButton.getXAdjusted(screen, style), screen.topPos + style.getData().y(), style.getData().width(), style.getData().height(), MutableComponent.create(PlainTextContents.EMPTY));
        this.screen = screen;
        this.style = style;
    }

    private static int getXAdjusted(AbstractContainerScreen<?> screen, OhmegaConfig.Client.Service.ButtonStyle style) {
        if (screen instanceof AccessoryInventoryScreen accScreen && OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            return screen.leftPos + style.getData().x() + accScreen.getExtraWidth();
        }

        return screen.leftPos + style.getData().x();
    }

    // todo: see if this can be changed
    private boolean isVisible() {
        return visible && (screen instanceof AccessoryInventoryScreen || (screen instanceof InventoryScreen inventoryScreen && !inventoryScreen.recipeBookComponent.isVisible()));
    }

    private void fixPos() {
        setX(getXAdjusted(screen, style));
        setY(screen.topPos + style.getData().y());
    }

    @Override
    protected boolean isValidClickButton(@NonNull MouseButtonInfo info) {
        return isVisible() && super.isValidClickButton(info);
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        Minecraft mc = screen.minecraft;
        LocalPlayer player = mc.player;

        if (player != null) {
            if (!player.isCreative() && !player.isSpectator()) {
                if (screen instanceof AccessoryInventoryScreen) {
                    player.containerMenu = player.inventoryMenu;
                    mc.setScreen(new InventoryScreen(player));
                    OhmegaNetworking.C2S.send(player, new ServerboundContainerClosePacket(player.containerMenu.containerId));
                } else {
                    ItemStack stack = player.containerMenu.getCarried();

                    // todo: see if we can carry the stack to the accessory inventory instead of this
                    if (!stack.isEmpty()) {
                        AbstractContainerMenu.dropOrPlaceInInventory(player, stack);
                        player.containerMenu.setCarried(ItemStack.EMPTY);
                    }

                    OhmegaNetworking.C2S.send(OpenAccessoryInventoryPacket.INSTANCE);
                }
            } else {
                player.containerMenu = player.inventoryMenu;
                mc.setScreen(new InventoryScreen(player));
                OhmegaNetworking.C2S.send(player, new ServerboundContainerClosePacket(player.containerMenu.containerId));
            }
        }
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor gui, int mx, int my, float partialTicks) {
        if (isVisible()) {
            fixPos();

            int hoveredOffsY;

            if ((style.highlightWhenHovered() && isHoveredOrFocused()) || screen instanceof AccessoryInventoryScreen) {
                hoveredOffsY = height;
            } else {
                hoveredOffsY = 0;
            }

            OhmegaConfig.Client.Service.ButtonStyle.Data data = style.getData();

            gui.blit(RenderPipelines.GUI_TEXTURED, style.getTextureLocation(), getX(), getY(), 0, hoveredOffsY, width, height, data.width(), data.height() * 2);
        }
    }

    // todo: change later
    @Override
    public void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
