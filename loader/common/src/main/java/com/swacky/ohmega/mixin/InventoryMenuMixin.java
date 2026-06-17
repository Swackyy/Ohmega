package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.api.common.menu.IMixinAccessoryMenu;
import com.swacky.ohmega.common.menu.TemporarySlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
abstract class InventoryMenuMixin extends AbstractCraftingMenu implements IMixinAccessoryMenu {
    @Unique
    private @Nullable AccessoryMenuExtension ohmega$extension = null;

    private InventoryMenuMixin(MenuType<?> type, int id, int width, int height) {
        super(type, id, width, height);
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

    // Uses a custom order to ensure this mixin is applied in the same order on client and server
    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"),
            order = -7777)
    private void init(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
        if (owner.level().isClientSide()) {
            AccessoryMenus.onConstruct(this, owner);
        } else {
            AccessoryMenus.attachExtension(this, owner, AccessoryMenus.assertImplementation(this));

            for (int i = 0; i < AccessoryHelper.getSlotTypes().size(); i++) {
                addSlot(new TemporarySlot());
            }
        }
    }

    // todo: this bad
    @Inject(
            method = "quickMoveStack",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private void quickMoveStack(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack candidate = AccessoryMenus.onQuickMoveStack(this, player, index);

        if (candidate != null) {
            cir.setReturnValue(candidate);
        }
    }
}
