package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.item.Accessory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
abstract class PiglinAiMixin {
    @ModifyReturnValue(
            method = "isWearingSafeArmor",
            at = @At(
                    value = "RETURN",
                    ordinal = 1))
    private static boolean isWearingSafeArmor(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        for (ItemStack stack : AccessoryHelper.getData(entity).getStacks()) {
            Accessory accessory = Accessories.get(stack.getItem());

            if (accessory != null && accessory.isPiglinSafe(stack)) {
                return true;
            }
        }

        return original;
    }
}
