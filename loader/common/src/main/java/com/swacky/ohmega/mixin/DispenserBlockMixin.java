package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.common.block.dispenser.AccessoryDispenseItemBehaviour;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @WrapOperation(
            method = "getDispenseMethod",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDefaultDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"))
    public DispenseItemBehavior getDispenseMethod(ItemStack stack, Operation<DispenseItemBehavior> handle) {
        if (Accessories.isBound(stack.getItem())) {
            return AccessoryDispenseItemBehaviour.getInstance();
        }

        return handle.call(stack);
    }
}
