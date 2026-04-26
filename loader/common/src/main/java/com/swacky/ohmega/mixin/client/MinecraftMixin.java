package com.swacky.ohmega.mixin.client;

import com.mojang.blaze3d.platform.WindowEventHandler;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
    private MinecraftMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(GameConfig config, CallbackInfo ci) {
        OhmegaHooks.accessoryBind();
    }
}
