package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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

@Mixin(RecipeBookComponent.class)
abstract class RecipeBookComponentMixin implements GuiEventListener, Renderable, NarratableEntry {
    @ModifyReturnValue(
            method = "updateScreenPosition",
            at = @At(
                    value = "RETURN"))
    private int updateScreenPosition(int original) {
        if (OhmegaConfig.Client.compatibilityMode() && Minecraft.getInstance().screen instanceof IAccessoryScreen screen) {
            AccessoryScreenExtension extension = screen.getAccessoryExtension();

            if (extension != null && screen.isAccessoryExtensionVisible()) {
                return original + extension.getExtraWidth() / 2;
            }
        }

        return original;
    }
}
