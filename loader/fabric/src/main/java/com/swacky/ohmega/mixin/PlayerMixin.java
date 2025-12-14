package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        AccessoryHelper.getContainer(((Player) (Object) this)).tick();
    }

    @Inject(method = "die", at = @At(value = "HEAD"))
    public void die(DamageSource damageSource, CallbackInfo ci) {
        AccessoryHelper.getContainer((Player) (Object) this).onDeath();
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        Player this$0 = (Player) (Object) this;
        this$0.getAttachedOrCreate(OhmegaDataAttachments.ACCESSORY_HANDLER).initialise(this$0);
    }
}
