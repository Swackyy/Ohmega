package com.swacky.ohmega.mixin;

import com.swacky.ohmega.common.menu.AccessorySlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

/**
 * Vanilla slot sync is both slow and doesn't support certain things Ohmega would like to do,
 * we redirect all synchronisation calls to custom handlers here
 */
@Mixin(AbstractContainerMenu.class)
abstract class AbstractContainerMenuMixin {
    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Inject(
            method = "synchronizeSlotToRemote",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ContainerSynchronizer;sendSlotChange(Lnet/minecraft/world/inventory/AbstractContainerMenu;ILnet/minecraft/world/item/ItemStack;)V"),
            cancellable = true)
    private void synchronizeSlotToRemote(int i, ItemStack current, Supplier<ItemStack> currentCopy, CallbackInfo ci) {
        if (slots.get(i) instanceof AccessorySlot) {
            ci.cancel();
        }
    }
}
