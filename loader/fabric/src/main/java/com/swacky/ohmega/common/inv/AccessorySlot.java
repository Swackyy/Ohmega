package com.swacky.ohmega.common.inv;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryEquipCallback;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

public class AccessorySlot extends Slot {
    private static final Container EMPTY_CONTAINER = new SimpleContainer(0);

    protected final Player player;
    protected final AccessoryContainer handler;
    protected final int slot;
    protected final AccessoryType type;

    public AccessorySlot(Player player, AccessoryContainer handler, int index, int x, int y, AccessoryType type) {
        super(EMPTY_CONTAINER, index, x, y);
        this.player = player;
        this.handler = handler;
        this.slot = index;
        this.type = type;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        IAccessory acc = AccessoryHelper.getBoundAccessory(item);
        if (acc != null) {
            return this.handler.isItemValid(this.slot, stack) && AccessoryHelper.getType(item) == this.type;
        }
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack stack = getItem();
        if (stack.isEmpty()) {
            return false;
        }

        boolean original = !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if (acc != null) {
            original &= acc.canUnequip(player, getItem());
        }

        return OhmegaHooks.accessoryCanUnequipEvent(player, getItem(), original);
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.handler.getStackInSlot(this.slot);
    }

    @Override
    public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {}

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getMaxStackSize();
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return this.handler.extractItem(this.slot, amount, false);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if (!hasItem() && acc != null) {
            AccessoryHelper.changeModifiers(player, AccessoryHelper.getModifiers(stack).getPassive(), false);

            if (!OhmegaHooks.accessoryUnequipEvent(this.player, stack).isCanceled()) {
                acc.onUnequip(this.player, stack);
            }
            AccessoryHelper.setSlot(stack, -1);
        }
        super.onTake(player, stack);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        IAccessory acc = AccessoryHelper.getBoundAccessory(getItem().getItem());
        if (hasItem() && stack != getItem() && acc != null) {
            AccessoryHelper.changeModifiers(this.player, AccessoryHelper.getModifiers(getItem()).getPassive(), false);

            if (!OhmegaHooks.accessoryUnequipEvent(this.player, getItem()).isCanceled()) {
                acc.onUnequip(this.player, getItem());
            }

            AccessoryHelper.setSlot(getItem(), -1);
            this.setChanged();
        }

        ItemStack old = getItem().copy();
        this.handler.setStackInSlot(this.slot, stack);
        this.setChanged();

        acc = AccessoryHelper.getBoundAccessory(getItem().getItem());
        if (hasItem() && old != getItem() && acc != null) {
            AccessoryHelper.setSlot(stack, this.slot);

            AccessoryHelper.changeModifiers(this.player, AccessoryHelper.getModifiers(stack).getPassive(), true);

            if (!OhmegaHooks.accessoryEquipEvent(this.player, stack, AccessoryEquipCallback.Context.SLOT_PLACE).isCanceled()) {
                acc.onEquip(this.player, stack);
            }
            this.setChanged();
        }
    }

    @Override
    public ResourceLocation getNoItemIcon() {
        return this.type.getEmptySlotLocation();
    }

    public AccessoryType getType() {
        return this.type;
    }
}