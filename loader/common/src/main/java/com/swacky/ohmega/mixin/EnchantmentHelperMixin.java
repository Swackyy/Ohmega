package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
    @Unique
    private static final EquipmentSlot[] HUMANOID_ARMOUR = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    // todo: evaluate the use of a better injector here, as this is both slower than needed and may introduce incompatibility
    @Inject(
            method = "getRandomItemWith",
            at = @At(
                    value = "RETURN"),
            cancellable = true)
    private static void getRandomItemWith(DataComponentType<?> dataType, LivingEntity entity, Predicate<ItemStack> filter, CallbackInfoReturnable<Optional<EnchantedItemInUse>> cir) {
        int original = 0;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);

            if (filter.test(stack)) {
                for (Holder<Enchantment> holder : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).keySet()) {
                    Enchantment enchantment = holder.value();

                    if (enchantment.effects().has(dataType) && enchantment.matchingSlot(slot)) {
                        original++;
                    }
                }
            }
        }

        ArrayList<EnchantedItemInUse> list = new ArrayList<>();

        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(entity).getEntries()) {
            ItemStack stack = entry.getStack();

            if (filter.test(stack)) {
                for (Holder<Enchantment> holder : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).keySet()) {
                    Enchantment enchantment = holder.value();

                    if (enchantment.effects().has(dataType) && ohmega$checkSlots(enchantment.definition().slots())) {
                        list.add(new EnchantedItemInUse(stack, EquipmentSlot.MAINHAND, entity));
                    }
                }
            }
        }

        if (!list.isEmpty()) {
            RandomSource random = entity.getRandom();

            if (random.nextInt(list.size() + original) >= original) {
                cir.setReturnValue(Util.getRandomSafe(list, random));
            }
        }
    }

    @Unique
    private static boolean ohmega$checkSlots(List<EquipmentSlotGroup> groups) {
        for (EquipmentSlotGroup group : groups) {
            for (EquipmentSlot slot : HUMANOID_ARMOUR) {
                if (group.test(slot)) {
                    return true;
                }
            }
        }

        return false;
    }
}
