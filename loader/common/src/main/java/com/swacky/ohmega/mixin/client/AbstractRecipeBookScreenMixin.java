package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
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

    // todo: maybe use a different injector?
    @ModifyReturnValue(
            method = "hasClickedOutside",
            at = @At(
                    value = "RETURN"))
    private boolean hasClickedOutside(boolean original, @Local(name = "mx") double mx, @Local(name = "my") double my) {
        if (original && this instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null) {
                return extension.hasClickedOutside(
                        mx - accessoryScreen.getAccessoryExtensionX() - leftPos,
                        my - accessoryScreen.getAccessoryExtensionY() - topPos);
            }
        }

        return original;
    }
}
