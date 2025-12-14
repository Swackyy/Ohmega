package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "die", at = @At(value = "HEAD"))
    public void die(DamageSource damageSource, CallbackInfo ci) {
        AccessoryHelper.getContainer((ServerPlayer) (Object) this).onDeath();
    }
}
