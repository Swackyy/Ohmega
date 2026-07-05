package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(Inventory.class)
abstract class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Inject(
            method = "clearContent",
            at = @At(
                    value = "RETURN"))
    private void clearContent(CallbackInfo ci) {
        if (OhmegaConfig.Server.getData().injectVanillaClear().get()) {
            OhmegaDataAttachments.getData(player).clearMatchingItems(player, null, -1, EquipContext.UNKNOWN);
        }
    }

    @ModifyReturnValue(
            method = "clearOrCountMatchingItems",
            at = @At(
                    value = "RETURN"))
    private int clearOrCountMatchingItems(int original, @Local(argsOnly = true) Predicate<ItemStack> filter, @Local(argsOnly = true) int max) {
        if (OhmegaConfig.Server.getData().injectVanillaClear().get()) {
            original += OhmegaDataAttachments.getData(player).clearMatchingItems(player, filter, max, EquipContext.UNKNOWN);
        }

        return original;
    }
}
