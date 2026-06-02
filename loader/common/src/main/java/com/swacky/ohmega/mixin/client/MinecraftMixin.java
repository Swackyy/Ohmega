package com.swacky.ohmega.mixin.client;

import com.mojang.blaze3d.platform.WindowEventHandler;
import com.swacky.ohmega.api.client.screen.IEmbeddingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
    @Shadow
    @Nullable
    public Screen screen;

    private MinecraftMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @Inject(
            method = "setScreen",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0),
            cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (this.screen instanceof IEmbeddingScreen embeddingScreen && !embeddingScreen.shouldAllowSetScreen()) {
            ci.cancel();
        }
    }
}
