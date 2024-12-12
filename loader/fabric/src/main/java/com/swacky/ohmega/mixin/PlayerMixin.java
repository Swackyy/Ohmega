package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
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
        Player this$0 = (Player) (Object) this;
        boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
            case ON -> false;
            case OFF -> true;
            case DEFAULT -> this$0.getServer() == null || !this$0.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        };

        if (this$0.getServer() != null && flag) {
            AccessoryHelper.getContainer(this$0).invalidate();
        }
    }
}
