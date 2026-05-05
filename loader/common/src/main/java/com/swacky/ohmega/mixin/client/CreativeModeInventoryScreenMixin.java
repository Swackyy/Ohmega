package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtensions;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IMixinAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IMixinEntityRenderingScreen;
import com.swacky.ohmega.api.common.menu.IAccessorySlotContainer;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements IMixinAccessoryScreen, IMixinEntityRenderingScreen {
    @Unique
    private @Nullable AccessoryScreenExtension ohmega$extension = null;

    @Shadow
    private static CreativeModeTab selectedTab;

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
    public IntIntPair getAccessoryExtensionToggleButtonPosition(OhmegaConfig.Client.Service.ButtonStyle style) {
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
        AccessoryScreenExtensions.onConstruct(this);
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

    // todo: maybe use a different injector?
    @Inject(
            method = "hasClickedOutside",
            at = @At(
                    value = "RETURN"),
            cancellable = true)
    private void hasClickedOutside(double mx, double my, int xo, int yo, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            AccessoryScreenExtension extension = getAccessoryExtension();

            if (extension != null) {
                hasClickedOutside = extension.hasClickedOutside(
                        mx - AccessoryScreenExtensions.getAccessoryExtensionX(this) - leftPos,
                        my - AccessoryScreenExtensions.getAccessoryExtensionY(this) - topPos);

                cir.setReturnValue(hasClickedOutside);
            }
        }
    }

    @WrapOperation(
            method = "selectTab",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/inventory/Slot;III)Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$SlotWrapper;"))
    private CreativeModeInventoryScreen.SlotWrapper selectTab(Slot slot, int index, int x, int y, Operation<CreativeModeInventoryScreen.SlotWrapper> handle) {
        if (slot instanceof AccessorySlot) {
            List<AccessorySlot> itemPickerMenuSlots = ((IAccessorySlotContainer) menu).getAccessoryExtensionSlots();

            if (itemPickerMenuSlots != null) {
                AccessorySlot itemPickerMenuSlot = itemPickerMenuSlots.get(slot.getContainerSlot());

                x = itemPickerMenuSlot.x;
                y = itemPickerMenuSlot.y;
            }
        }

        return handle.call(slot, index, x, y);
    }
}
