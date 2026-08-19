package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IEmbeddingScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.common.event.OhmegaHooks;
import net.minecraft.client.Minecraft;
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
    // todo: reevaluate this being a screen mixin and not in the abstractcontainerscreen one
    @Inject(
            method = "extractRenderStateWithTooltipAndSubtitles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;nextStratum()V",
                    ordinal = 1))
    private void extractBackground(GuiGraphicsExtractor gui, int mx, int my, float partialTicks, CallbackInfo ci) {
        Screen screen = AccessoryScreens.getEffectiveScreen();

        if (screen instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null && accessoryScreen.isAccessoryExtensionVisible() && (Object) screen instanceof AbstractContainerScreen<?> containerScreen) {
                Matrix3x2fStack pose = gui.pose();

                pose.pushMatrix();

                LazyPosition position = accessoryScreen.getAccessoryExtensionPosition();

                pose.translate(
                        position.x().get() + containerScreen.leftPos,
                        position.y().get() + containerScreen.topPos);

                if (!OhmegaHooks.renderAccessoryExtensionPre(gui, extension)) {
                    extension.extractExtension(gui);
                    OhmegaHooks.renderAccessoryExtensionPost(gui, extension);
                }

                pose.popMatrix();
            }
        }
    }

    @Inject(
            method = "extractTransparentBackground",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private void extractTransparentBackground(GuiGraphicsExtractor gui, CallbackInfo ci) {
        if (Minecraft.getInstance().gui.screen() instanceof IEmbeddingScreen embeddingScreen && !embeddingScreen.allowDimBackground()) {
            ci.cancel();
        }
    }
}
