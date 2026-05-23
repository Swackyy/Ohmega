package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.api.common.menu.IMixinAccessoryMenu;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
abstract class ItemPickerMenuMixin extends AbstractContainerMenu implements IMixinAccessoryMenu {
    @Unique
    private @Nullable AccessoryMenuExtension ohmega$extension = null;

    @Shadow
    @Final
    private AbstractContainerMenu inventoryMenu;

    private ItemPickerMenuMixin(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @Nullable AccessoryMenuExtension getAccessoryExtension() {
        return ohmega$extension;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setAccessoryExtension(@NonNull AccessoryMenuExtension extension) {
        ohmega$extension = extension;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public boolean isAccessoryExtensionVisible() {
        return ((IAccessoryMenu) inventoryMenu).isAccessoryExtensionVisible();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setAccessoryExtensionVisible(boolean value) {
        ((IAccessoryMenu) inventoryMenu).setAccessoryExtensionVisible(value);
    }

    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void init(Player owner, CallbackInfo ci) {
        AccessoryMenuExtension extension = AccessoryMenus.setExtension(this, owner);

        if (extension != null) {
            extension.setSlots(AccessoryMenus.getAccessorySlots(this, owner));
        }
    }
}