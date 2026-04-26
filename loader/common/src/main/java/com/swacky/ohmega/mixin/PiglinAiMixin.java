package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.common.item.Accessory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
abstract class PiglinAiMixin {
    @Inject(
            method = "isWearingSafeArmor",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private static void isWearingSafeArmor(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack stack : AccessoryHelper.getAccessoryStacks(entity)) {
            Accessory accessory = Accessories.get(stack.getItem());

            if (accessory != null && accessory.isPiglinSafe(stack)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
