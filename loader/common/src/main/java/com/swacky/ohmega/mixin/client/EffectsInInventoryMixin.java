package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EffectsInInventory.class)
abstract class EffectsInInventoryMixin {
    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;

    @Definition(id = "screen", field = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;screen:Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;")
    @Definition(id = "imageWidth", field = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;imageWidth:I")
    @Definition(id = "leftPos", field = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;leftPos:I")
    @Expression("this.screen.leftPos + this.screen.imageWidth + 2")
    @ModifyExpressionValue(
            method = "extractRenderState",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION"))
    public int extractRenderState(int original) {
        if (screen instanceof IAccessoryScreen accessoryScreen && accessoryScreen.isAccessoryExtensionVisible()) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null) {
                return original + extension.getExtraWidthRight() + 2;
            }
        }

        return original;
    }
}
