package com.swacky.ohmega.common.inv;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.dataattachment.AccessoryInvDataAttachment;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class AccessoryContainer {
    private final Player player;
    private final AccessoryInvDataAttachment data;

    @ApiStatus.Internal
    public AccessoryContainer(Player player, AccessoryInvDataAttachment data) {
        this.player = player;
        this.data = data;
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot >= 0 && slot < data.getSlots()) {
            Item item = stack.getItem();
            IAccessory acc = AccessoryHelper.getBoundAccessory(item);
            if (acc != null && (AccessoryHelper.compatibleWith(this.player, acc) || ItemStack.isSameItem(stack, this.getStackInSlot(slot)))) {
                return OhmegaHooks.accessoryCanEquipEvent(this.player, stack, acc.canEquip(this.player, stack)) && AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(slot);
            }
        }
        return false;
    }

    public int getSlots() {
        return this.data.getSlots();
    }

    public void onContentsChanged(int index) {
        this.data.onContentsChanged(index);
    }

    public ItemStack getStackInSlot(int index) {
        return this.data.getStackInSlot(index);
    }

    public boolean setStackInSlot(int index, ItemStack stack) {
        if (stack.isEmpty() || this.isItemValid(index, stack) && AccessoryHelper.isItemAccessoryBound(stack.getItem())) {
            this.data.setStackInSlot(index, stack);
            return true;
        }
        return false;
    }

    public ItemStack removeItem(int index, int amount) {
        return this.data.removeItem(index, amount);
    }

    public void tick() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
            if (acc != null && !OhmegaHooks.accessoryTickEventPre(this.player, stack)) {
                acc.tick(this.player, stack);
                OhmegaHooks.accessoryTickEventPost(this.player, stack);
            }
        }
        this.sync();
    }

    private void sync() {
        this.data.sync(this.player);
    }

    @ApiStatus.Internal
    public void invalidate() {
        this.data.invalidate(this.player);
    }

    @ApiStatus.Internal
    public void reloadCfg() {
        this.data.reloadCfg(this.player);
    }
}