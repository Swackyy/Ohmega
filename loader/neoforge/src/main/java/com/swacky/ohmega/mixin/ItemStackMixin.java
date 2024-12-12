package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.ModifierHolder;
import com.swacky.ohmega.common.datacomponent.AccessoryItemDataComponent;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At(value = "RETURN"))
    private void ItemStack(ItemLike itemLike, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        if (itemLike instanceof Item item) {
            IAccessory acc = AccessoryHelper.getBoundAccessory(item);
            if (acc != null) {
                AccessoryItemDataComponent data = AccessoryHelper._getInternalData((ItemStack) (Object) this);
                if (data != null) {
                    ModifierHolder.Builder builder = new ModifierHolder.Builder();
                    acc.addDefaultAttributeModifiers(builder);
                    OhmegaHooks.accessoryAttributeModifiersEvent(itemLike.asItem(), builder);
                    data.setModifiers(builder.build());
                }
            }
        }
    }
}
