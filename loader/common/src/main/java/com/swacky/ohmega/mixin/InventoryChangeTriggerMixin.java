package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryChangeTrigger.class)
abstract class InventoryChangeTriggerMixin extends SimpleCriterionTrigger<InventoryChangeTrigger.TriggerInstance> {
    @WrapOperation(
            method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancements/triggers/InventoryChangeTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;III)V"))
    private void trigger(InventoryChangeTrigger trigger, ServerPlayer player, Inventory inventory, ItemStack changed, int slotsFull, int slotsEmpty, int slotsOccupied, Operation<Void> handle) {
        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(player).getEntries()) {
            ItemStack stack = entry.getStack();

            if (stack.isEmpty()) {
                slotsEmpty++;
            } else {
                slotsOccupied++;

                if (stack.getCount() >= stack.getMaxStackSize()) {
                    slotsFull++;
                }
            }
        }

        handle.call(trigger, player, inventory, changed, slotsFull, slotsEmpty, slotsOccupied);
    }
}
