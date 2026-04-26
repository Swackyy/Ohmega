package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtensions;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRecipeBookScreen.class)
abstract class AbstractRecipeBookScreenMixin<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
    private AbstractRecipeBookScreenMixin(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;extractCarriedItem(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"))
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

    @Inject(
            method = "hasClickedOutside",
            at = @At(
                    value = "RETURN"),
            cancellable = true)
    private void hasClickedOutside(double mx, double my, int xo, int yo, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && this instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null) {
                cir.setReturnValue(extension.hasClickedOutside(
                        mx - AccessoryScreenExtensions.getAccessoryExtensionX(accessoryScreen) - leftPos,
                        my - AccessoryScreenExtensions.getAccessoryExtensionY(accessoryScreen) - topPos));
            }
        }
    }
}
