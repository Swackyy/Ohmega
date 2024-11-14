package com.swacky.ohmega.common.inv;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.api.event.AccessoryUnequipEvent;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.items.SlotItemHandler;
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
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack stack = getItem();
        boolean original = EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if (acc != null) {
            original = acc.canUnequip(player, getItem());
        }
        return !getItem().isEmpty() && OhmegaHooks.accessoryCanUnequipEvent(player, getItem(), original).getReturnValue();
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if (!hasItem() && acc != null) {
            AccessoryHelper.changeModifiers(player, AccessoryHelper.getModifiers(stack).getPassive(), false);

            AccessoryUnequipEvent event = OhmegaHooks.accessoryUnequipEvent(this.player, stack);
            if (!event.isCanceled()) {
                acc.onUnequip(this.player, stack);
            }
            AccessoryHelper.setSlot(stack, -1);
            AccessoryHelper.setActive(this.player, stack, false);
        }
        super.onTake(player, stack);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        IAccessory acc = AccessoryHelper.getBoundAccessory(getItem().getItem());
        if (hasItem() && stack != getItem() && acc != null) {
            AccessoryHelper.changeModifiers(this.player, AccessoryHelper.getModifiers(getItem()).getPassive(), false);

            AccessoryUnequipEvent event = OhmegaHooks.accessoryUnequipEvent(this.player, getItem());
            if (!event.isCanceled()) {
                acc.onUnequip(this.player, getItem());
            }

            AccessoryHelper.setSlot(getItem(), -1);
            AccessoryHelper.setActive(this.player, getItem(), false);
            this.setChanged();
        }

        ItemStack old = getItem().copy();
        super.set(stack);

        acc = AccessoryHelper.getBoundAccessory(getItem().getItem());
        if (hasItem() && old != getItem() && acc != null) {
            AccessoryHelper.setSlot(stack, this.slot);

            AccessoryHelper.changeModifiers(this.player, AccessoryHelper.getModifiers(stack).getPassive(), true);

            AccessoryEquipEvent event = OhmegaHooks.accessoryEquipEvent(this.player, stack, AccessoryEquipEvent.Context.SLOT_PLACE);
            if (!event.isCanceled()) {
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