package com.swacky.ohmega.compat.rrv.mixin;

import cc.cassian.rrv.fabric.FabricClientEntrypoint;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FabricClientEntrypoint.class)
abstract class FabricClientEntrypointMixin {
    @Inject(
            method = "onInitializeClient -> *(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;II)V",
            at = @At(
                    value = "HEAD"))
    private static void onInitializeClient(Minecraft client, Screen screen, int scaledWidth, int scaledHeight, CallbackInfo ci, @Local(argsOnly = true) LocalRef<Screen> screenLocalRef) {
        screenLocalRef.set(AccessoryScreens.getEffectiveScreen(screen));
    }
}
