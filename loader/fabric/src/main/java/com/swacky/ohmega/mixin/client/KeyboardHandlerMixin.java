package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "keyPress",
            at = @At(
                    value = "TAIL"))
    private void keyPress(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (window == minecraft.getWindow().handle()) {
            ClientCallbacks.onKeyInput();
        }
    }
}
