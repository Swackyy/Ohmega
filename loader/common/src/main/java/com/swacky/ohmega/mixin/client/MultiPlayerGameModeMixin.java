package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.Accessory;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// todo: see if this can be moved to one ItemStackMixin or something
@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeMixin {
    @WrapOperation(
            method = "lambda$useItem$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult useItem(ItemStack stack, Level level, Player player, InteractionHand hand, Operation<InteractionResult> handle) {
        InteractionResult original = handle.call(stack, level, player, hand);

        if (!original.consumesAction()) {
            Accessory accessory = Accessories.get(stack.getItem());

            if (accessory != null && accessory.preferVanillaUse(stack)) {
                InteractionResult candidate = AccessoryHelper.tryEquip(player, stack);

                if (candidate.consumesAction()) {
                    return candidate;
                }
            }
        }

        return original;
    }
}
