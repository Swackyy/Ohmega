package com.swacky.ohmega.common.core.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "RETURN"))
    private void ItemStack(ItemLike item, int count, CompoundTag tag, CallbackInfo ci) {
        if(item instanceof IAccessory acc) {
            IAccessory.ModifierBuilder builder = new IAccessory.ModifierBuilder();
            acc.addDefaultAttributeModifiers(builder);
            OhmegaHooks.accessoryAttributeModifiersEvent(item.asItem(), builder);
            AccessoryHelper._internalTag((ItemStack) (Object) (this)).put("AccessoryAttributeModifiers", builder.serialize());
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "RETURN"))
    private void ItemStack(CompoundTag tag, CallbackInfo ci) {
        if(getItem() instanceof IAccessory acc) {
            IAccessory.ModifierBuilder builder = new IAccessory.ModifierBuilder();
            acc.addDefaultAttributeModifiers(builder);
            OhmegaHooks.accessoryAttributeModifiersEvent(getItem(), builder);
            AccessoryHelper._internalTag((ItemStack) (Object) (this)).put("AccessoryAttributeModifiers", builder.serialize());
        }
    }
}
