package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
abstract class PowderSnowBlockMixin {
    @Inject(
            method = "canEntityWalkOnPowderSnow",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private static void canEntityWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // todo
        if (entity instanceof Player player) {
            for (ItemStack stack : AccessoryHelper.getAccessoryStacks(player)) {
                if (OhmegaHooks.allowWalkOnPowderSnow(stack, AccessoryHelper.getAccessory(stack.getItem()).allowWalkOnPowderSnow(stack))) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
