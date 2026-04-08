package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// todo: see if this can be moved to one ItemStackMixin or something
@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeMixin {
    @Inject(
            method = "useItem",
            at = @At(
                    value = "RETURN"),
            cancellable = true)
    private void useItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!cir.getReturnValue().consumesAction()) {
            ItemStack stack = player.getItemInHand(hand);
            Item item = stack.getItem();

            if (AccessoryHelper.isAccessory(item) && OhmegaHooks.preferVanillaUse(stack, AccessoryHelper.getAccessory(item).preferVanillaUse(stack))) {
                InteractionResult candidate = AccessoryHelper.tryEquip(player, stack);

                if (candidate.consumesAction()) {
                    cir.setReturnValue(candidate);
                }
            }
        }
    }
}
