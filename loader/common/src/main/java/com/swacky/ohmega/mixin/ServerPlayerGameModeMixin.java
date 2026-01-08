package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.AccessoryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
abstract class ServerPlayerGameModeMixin {
    @ModifyExpressionValue(
            method = "useItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult useItem(InteractionResult original, @Local(argsOnly = true) ServerPlayer player, @Local(argsOnly = true) InteractionHand hand) {
        if (!original.consumesAction()) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, hand);

            if (candidate.consumesAction()) {
                return candidate;
            }
        }

        return original;
    }
}
