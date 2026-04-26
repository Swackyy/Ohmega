package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.common.block.dispenser.AccessoryDispenseItemBehaviour;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Inject(
            method = "getDispenseMethod",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDefaultDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"),
            cancellable = true)
    public void getDispenseMethod(Level level, ItemStack stack, CallbackInfoReturnable<DispenseItemBehavior> cir) {
        if (Accessories.isBound(stack.getItem())) {
            cir.setReturnValue(AccessoryDispenseItemBehaviour.getInstance());
        }
    }
}
