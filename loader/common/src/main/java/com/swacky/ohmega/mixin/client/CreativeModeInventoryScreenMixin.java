package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IMixinAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IMixinEntityRenderingScreen;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements IMixinAccessoryScreen, IMixinEntityRenderingScreen {
    @Unique
    private @Nullable AccessoryScreenExtension ohmega$extension = null;

    @Shadow
    public static CreativeModeTab selectedTab;

    @Shadow
    private boolean hasClickedOutside;

    private CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @Nullable AccessoryScreenExtension getAccessoryExtension() {
        return ohmega$extension;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setAccessoryExtension(@NonNull AccessoryScreenExtension extension) {
        ohmega$extension = extension;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull IntLazySavedValue getAccessoryExtensionX() {
        return OhmegaConfig.Client.getData().creativeExtensionX();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull IntLazySavedValue getAccessoryExtensionY() {
        return OhmegaConfig.Client.getData().creativeExtensionY();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull IntIntPair getAccessoryExtensionToggleButtonPosition(OhmegaConfig.Client.Service.ButtonStyle style) {
        return switch (style) {
            case DEFAULT -> IntIntPair.of(137, 19);
            case LEGACY -> IntIntPair.of(74, 7);
            case TAG_LEFT -> IntIntPair.of(-11, 8);
            case TAG_RIGHT -> IntIntPair.of(192, 8);
            default -> throw new IllegalStateException("Unexpected value: " + style);
        };
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull IntIntPair getFlipEntityButtonPosition() {
        return IntIntPair.of(95, 7);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public boolean areAccessoryExtensionWidgetsVisible() {
        // Forge complains with an interface super call
        AccessoryScreenExtension extension = getAccessoryExtension();

        if (extension != null) {
            return extension.areWidgetsVisible() && selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
        }

        return false;
    }

    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void init(LocalPlayer owner, FeatureFlagSet flags, boolean displayOperatorTab, CallbackInfo ci) {
        AccessoryScreens.onConstruct(this);
    }

    @ModifyArg(
            method = "extractBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;extractEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"),
            index = 5)
    private int extractBackground(int size) {
        // Hacky thing, shouldn't cause issues
        if (ohmega$extension instanceof IEntityRenderingExtension extension && extension.isEntityFlipped()) {
            return -size;
        }

        return size;
    }

    @ModifyReturnValue(
            method = "hasClickedOutside",
            at = @At(
                    value = "RETURN"))
    private boolean hasClickedOutside(boolean original, @Local(name = "mx") double mx, @Local(name = "my") double my) {
        if (original) {
            AccessoryScreenExtension extension = getAccessoryExtension();

            if (extension != null) {
                hasClickedOutside = extension.hasClickedOutside(
                        mx - getAccessoryExtensionX().get() - leftPos,
                        my - getAccessoryExtensionY().get() - topPos);

                return hasClickedOutside;
            }
        }

        return original;
    }

    @WrapOperation(
            method = "selectTab",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/inventory/Slot;III)Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$SlotWrapper;"))
    private CreativeModeInventoryScreen.SlotWrapper selectTab(Slot slot, int index, int x, int y, Operation<CreativeModeInventoryScreen.SlotWrapper> handle) {
        if (menu instanceof IAccessoryMenu accessoryMenu) {
            AccessoryMenuExtension extension = accessoryMenu.getAccessoryExtension();

            if (extension != null && slot instanceof AccessorySlot) {
                slot = extension.getSlots().get(slot.getContainerSlot());
                slot.index = index;
                x = slot.x;
                y = slot.y;
            }
        }

        return handle.call(slot, index, x, y);
    }
}
