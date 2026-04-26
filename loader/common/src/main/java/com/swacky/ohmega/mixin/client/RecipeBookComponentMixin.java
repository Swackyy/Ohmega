package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
abstract class RecipeBookComponentMixin implements GuiEventListener, Renderable, NarratableEntry {
    @Inject(
            method = "updateScreenPosition",
            at = @At(
                    value = "RETURN"),
            cancellable = true)
    private void updateScreenPosition(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
        if (OhmegaConfig.Client.compatibilityMode() && Minecraft.getInstance().screen instanceof IAccessoryScreen screen) {
            AccessoryScreenExtension extension = screen.getAccessoryExtension();

            if (extension != null && screen.isAccessoryExtensionVisible()) {
                cir.setReturnValue(cir.getReturnValue() + extension.getExtraWidth() / 2);
            }
        }
    }
}
