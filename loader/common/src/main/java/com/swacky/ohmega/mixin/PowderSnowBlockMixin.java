package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.item.Accessory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
abstract class PowderSnowBlockMixin extends Block implements BucketPickup {
    private PowderSnowBlockMixin(Properties properties) {
        super(properties);
    }

    @ModifyReturnValue(
            method = "canEntityWalkOnPowderSnow",
            at = @At(
                    value = "RETURN",
                    ordinal = 1))
    private static boolean canEntityWalkOnPowderSnow(boolean original, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof LivingEntity living) {
            for (ItemStack stack : AccessoryHelper.getData(living).getStacks()) {
                Accessory accessory = Accessories.get(stack.getItem());

                if (accessory != null && accessory.allowWalkOnPowderSnow(stack)) {
                    return true;
                }
            }
        }

        return original;
    }
}
