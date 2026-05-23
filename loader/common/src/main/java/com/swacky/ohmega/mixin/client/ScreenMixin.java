package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable {
    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "extractBackground",
            at = @At(
                    value = "TAIL"))
    private void extractBackground(GuiGraphicsExtractor gui, int mx, int my, float partialTicks, CallbackInfo ci) {
        if (this instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null && accessoryScreen.isAccessoryExtensionVisible() && (Object) this instanceof AbstractContainerScreen<?> screen) {
                Matrix3x2fStack pose = gui.pose();

                pose.pushMatrix();
                pose.translate(
                        accessoryScreen.getAccessoryExtensionX() + screen.leftPos,
                        accessoryScreen.getAccessoryExtensionY() + screen.topPos);
                extension.extractExtension(gui);

                pose.popMatrix();
            }
        }
    }
}
