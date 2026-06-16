package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.screen.IEmbeddingScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class GuiMixin {
    @Shadow
    public abstract @Nullable Screen screen();

    @Inject(
            method = "setScreen",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0),
            cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (screen() instanceof IEmbeddingScreen embeddingScreen && !embeddingScreen.shouldAllowSetScreen()) {
            ci.cancel();
        }
    }
}
