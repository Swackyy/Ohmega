package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
abstract class EntityMixin {
    @Inject(
            method = "load",
            at = @At(
                    value = "RETURN"))
    private void load(ValueInput input, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            InventoryMenu menu = player.inventoryMenu;
            AccessoryMenuExtension extension = AccessoryMenus.assertImplementation(menu).getAccessoryExtension();

            if (extension != null) {
                extension.setSlots(AccessoryMenus.createSlots(menu, player, menu::addSlot));
            }
        }
    }
}
