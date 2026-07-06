package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(InventoryChangeTrigger.TriggerInstance.class)
abstract class InventoryChangeTrigger_TriggerInstanceMixin implements SimpleCriterionTrigger.SimpleInstance {
    @WrapOperation(
            method = "matches",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;isEmpty()Z",
                    ordinal = 1))
    private boolean matches(List<ItemPredicate> list, Operation<Boolean> handle, @Local(argsOnly = true) Inventory inventory) {
        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(inventory.player).getEntries()) {
            if (list.isEmpty()) {
                return true;
            }

            ItemStack stack = entry.getStack();

            if (!stack.isEmpty()) {
                list.removeIf(predicate -> predicate.test(stack));
            }
        }

        return handle.call(list);
    }
}
