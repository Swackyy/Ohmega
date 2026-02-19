package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
    @Inject(
            method = "getRandomItemWith(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;",
            at = @At(
                    value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false)
    private static void getRandomItemWith(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> filter, CallbackInfoReturnable<Map.Entry<EquipmentSlot, ItemStack>> cir, Map<EquipmentSlot, ItemStack> map, ArrayList<Map.Entry<EquipmentSlot, ItemStack>> list) {
        if (entity instanceof Player player) {
            for (ItemStack stack : AccessoryHelper.getStacks(player)) {
                if (filter.test(stack)) {
                    list.add(Map.entry(EquipmentSlot.MAINHAND, stack));
                }
            }
        }
    }
}