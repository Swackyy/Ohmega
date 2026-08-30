package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IEmbeddingScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.common.item.IAccessory;
import com.swacky.ohmega.api.common.menu.AccessorySlot;
import com.swacky.ohmega.api.common.menu.IAccessorySlot;
import com.swacky.ohmega.api.config.OhmegaConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Shadow
    @Final
    protected T menu;

    @Shadow
    public int leftPos;

    @Shadow
    public int topPos;

    private AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractCarriedItem(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"))
    private void extractRenderState(GuiGraphicsExtractor gui, int mx, int my, float partialTicks, CallbackInfo ci) {
        if (this instanceof IAccessoryScreen screen) {
            AccessoryScreenExtension extension = screen.getAccessoryExtension();

            if (extension != null && screen.isAccessoryExtensionVisible()) {
                for (AbstractWidget widget : extension.getOverlayWidgets()) {
                    widget.extractRenderState(gui, mx, my, partialTicks);
                }
            }
        }
    }

    @ModifyExpressionValue(
            method = "extractSlotHighlightBack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;isHighlightable()Z"))
    private boolean extractSlotHighlightBack(boolean original) {
        if (this instanceof IAccessoryScreen) {
            return original && !ohmega$isHoveringOverlayWidget();
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "extractSlotHighlightFront",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;isHighlightable()Z"))
    private boolean extractSlotHighlightFront(boolean original) {
        if (this instanceof IAccessoryScreen) {
            return original && !ohmega$isHoveringOverlayWidget();
        }

        return original;
    }

    // todo: maybe use a different injector?
    @Inject(
            method = "extractTooltip",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private void extractTooltip(GuiGraphicsExtractor gui, int mx, int my, CallbackInfo ci) {
        if (this instanceof IAccessoryScreen) {
            if (ohmega$isHoveringOverlayWidget()) {
                ci.cancel();
                return;
            }

            if (
                    OhmegaConfig.Client.getData().showHoverTooltip().get() &&
                    hoveredSlot instanceof IAccessorySlot slot &&
                    !slot.hasItem() &&
                    menu.getCarried().isEmpty()) {
                gui.setTooltipForNextFrame(slot.getType().getTranslation(), mx, my);
            }
        }
    }

    @ModifyReturnValue(
            method = "hasClickedOutside",
            at = @At(
                    value = "RETURN"))
    private boolean hasClickedOutside(boolean original, @Local(name = "mx") double mx, @Local(name = "my") double my) {
        if (original && this instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null) {
                LazyPosition position = accessoryScreen.getAccessoryExtensionPosition();

                return extension.hasClickedOutside(
                        mx - position.x().get() - leftPos,
                        my - position.y().get() - topPos);
            }
        }

        return original;
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE", target = "Ljava/util/List;clear()V"))
    private void init(CallbackInfo ci) {
        if (OhmegaConfig.Client.getData().compatibilityMode().get() && this instanceof IAccessoryScreen screen) {
            AccessoryScreenExtension extension = screen.getAccessoryExtension();

            if (extension != null && screen.isAccessoryExtensionVisible()) {
                if (!((Object) this instanceof AbstractRecipeBookScreen<?>)) {
                    leftPos += extension.getExtraWidth() / 2;
                }

                topPos += extension.getExtraHeight() / 2;
            }
        }
    }

    @Inject(
            method = "isHovering(IIIIDD)Z",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    public void isHovering(int x, int y, int width, int height, double mx, double my, CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.gui.screen() instanceof IEmbeddingScreen && !(this instanceof IEmbeddingScreen)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean ohmega$isHoveringOverlayWidget() {
        if (this instanceof IAccessoryScreen screen) {
            AccessoryScreenExtension extension = screen.getAccessoryExtension();

            if (extension != null) {
                for (AbstractWidget widget : extension.getOverlayWidgets()) {
                    if (widget.isHovered()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
