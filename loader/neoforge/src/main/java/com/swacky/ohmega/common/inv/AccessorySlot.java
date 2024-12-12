package com.swacky.ohmega.common.inv;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class AccessorySlot extends SlotItemHandler {
    protected final Player player;
    protected final int slot;
    protected final AccessoryType type;

    public AccessorySlot(Player player, AccessoryContainer handler, int index, int x, int y, AccessoryType type) {
        super(handler, index, x, y);
        this.player = player;
        this.slot = index;
        this.type = type;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        Item item = stack.getItem();
        IAccessory acc = AccessoryHelper.getBoundAccessory(item);
        if (acc != null) {
            return getItemHandler().isItemValid(this.slot, stack) && AccessoryHelper.getType(item) == this.type;
        }
        return false;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        ItemStack stack = getItem();
        if (stack.isEmpty()) {
            return false;
        }

        boolean original = !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if (acc != null) {
            original &= acc.canUnequip(player, getItem());
        }

        return OhmegaHooks.accessoryCanUnequipEvent(player, getItem(), original).getReturnValue();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
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
        super.set(stack);

        acc = AccessoryHelper.getBoundAccessory(getItem().getItem());
        if (hasItem() && old != getItem() && acc != null) {
            AccessoryHelper.setSlot(stack, this.slot);

            AccessoryHelper.changeModifiers(this.player, AccessoryHelper.getModifiers(stack).getPassive(), true);

            if (!OhmegaHooks.accessoryEquipEvent(this.player, stack, AccessoryEquipEvent.Context.SLOT_PLACE).isCanceled()) {
                acc.onEquip(this.player, stack);
            }
            this.setChanged();
        }
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, this.type.getEmptySlotLocation());
    }

    public AccessoryType getType() {
        return this.type;
    }
}