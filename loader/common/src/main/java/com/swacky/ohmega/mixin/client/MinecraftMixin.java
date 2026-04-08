package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void init(GameConfig config, CallbackInfo ci) {
        OhmegaHooks.accessoryBindEvent();
    }
}
