package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IMixinAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IMixinEntityRenderingScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.client.screen.SnapLine;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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
    public @NonNull LazyPosition getAccessoryExtensionPosition() {
        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();

        return new LazyPosition(
                data.creativeExtensionX(),
                data.creativeExtensionY());
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull LazyPosition getAccessoryExtensionToggleButtonPosition(OhmegaConfig.Client.Service.ButtonStyle style) {
        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();

        return switch (style) {
            case DEFAULT -> new LazyPosition(data.creativeToggleExtensionButtonDefaultX(), data.creativeToggleExtensionButtonDefaultY());
            case LEGACY -> new LazyPosition(data.creativeToggleExtensionButtonLegacyX(), data.creativeToggleExtensionButtonLegacyY());
            case TAG_LEFT -> new LazyPosition(data.creativeToggleExtensionButtonTagLeftX(), data.creativeToggleExtensionButtonTagLeftY());
            case TAG_RIGHT -> new LazyPosition(data.creativeToggleExtensionButtonTagRightX(), data.creativeToggleExtensionButtonTagRightY());
            default -> throw new IllegalStateException("Unexpected value: " + style);
        };
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

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull LazyPosition getFlipEntityButtonPosition() {
        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();

        return new LazyPosition(data.creativeFlipEntityButtonX(), data.creativeFlipEntityButtonY());
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @Nullable List<SnapLine> getSnapLines(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryScreenExtension extension) {
        int x = screen.leftPos;
        int y = screen.topPos;

        return List.of(
                new SnapLine(true, x + 72),
                new SnapLine(false, y + 5),
                new SnapLine(true, x + 105),
                new SnapLine(false, y + 49));
    }

    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void init(LocalPlayer owner, FeatureFlagSet flags, boolean displayOperatorTab, CallbackInfo ci) {
        AccessoryScreens.onConstruct(this);
    }

    @ModifyReturnValue(
            method = "hasClickedOutside",
            at = @At(
                    value = "RETURN"))
    private boolean hasClickedOutside(boolean original, @Local(name = "mx") double mx, @Local(name = "my") double my) {
        if (original) {
            AccessoryScreenExtension extension = getAccessoryExtension();

            if (extension != null) {
                LazyPosition position = getAccessoryExtensionPosition();
                hasClickedOutside = extension.hasClickedOutside(
                        mx - position.x().get() - leftPos,
                        my - position.y().get() - topPos);

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
        if (menu instanceof IAccessoryMenu accessoryMenu && slot instanceof AccessorySlot) {
            List<AccessorySlot> slots = accessoryMenu.getSlots();

            if (slots != null) {
                slot = slots.get(slot.getContainerSlot());
                slot.index = index;
                x = slot.x;
                y = slot.y;
            }
        }

        return handle.call(slot, index, x, y);
    }
}
