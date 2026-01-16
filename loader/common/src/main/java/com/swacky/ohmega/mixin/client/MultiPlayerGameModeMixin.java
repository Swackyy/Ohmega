package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.AccessoryHelper;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeMixin {
    @Inject(
            method = "useItem",
            at = @At(
                    value = "RETURN"),
            cancellable = true)
    private void useItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!cir.getReturnValue().consumesAction()) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, hand);

            if (candidate.consumesAction()) {
                cir.setReturnValue(candidate);
            }
        }
    }
}
