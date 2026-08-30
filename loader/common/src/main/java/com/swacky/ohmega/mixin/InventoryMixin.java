package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.AccessoryHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @ModifyReturnValue(
            method = "contains(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(
                    value = "RETURN",
                    ordinal = 1))
    private boolean contains(boolean original, @Local(argsOnly = true) ItemStack searchStack) {
        for (ItemStack stack : AccessoryHelper.getStacks(player)) {
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, searchStack)) {
                return true;
            }
        }

        return original;
    }

    @ModifyReturnValue(
            method = "contains(Lnet/minecraft/tags/TagKey;)Z",
            at = @At(
                    value = "RETURN",
                    ordinal = 1))
    private boolean contains(boolean original, @Local(argsOnly = true) TagKey<Item> tag) {
        for (ItemStack stack : AccessoryHelper.getStacks(player)) {
            if (!stack.isEmpty() && stack.is(tag)) {
                return true;
            }
        }

        return original;
    }

    @ModifyReturnValue(
            method = "contains(Ljava/util/function/Predicate;)Z",
            at = @At(
                    value = "RETURN",
                    ordinal = 1))
    private boolean contains(boolean original, @Local(argsOnly = true) Predicate<ItemStack> filter) {
        for (ItemStack stack : AccessoryHelper.getStacks(player)) {
            if (filter.test(stack)) {
                return true;
            }
        }

        return original;
    }
}