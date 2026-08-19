package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
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
        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(entity).getEntries()) {
            ItemStack stack = entry.getStack();
            Accessory accessory = Accessories.get(stack.getItem());

            if (accessory != null && accessory.isPiglinSafe(stack)) {
                return true;
            }
        }

        return original;
    }
}
