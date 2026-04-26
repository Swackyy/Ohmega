package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.common.item.Accessory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
abstract class PowderSnowBlockMixin extends Block implements BucketPickup {
    private PowderSnowBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "canEntityWalkOnPowderSnow",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private static void canEntityWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity living) {
            for (ItemStack stack : AccessoryHelper.getAccessoryStacks(living)) {
                Accessory accessory = Accessories.get(stack.getItem());

                if (accessory != null && accessory.allowWalkOnPowderSnow(stack)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
