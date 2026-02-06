package com.swacky.ohmega.common.inv;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jspecify.annotations.NonNull;

public final class AccessorySlot extends Slot {
    private static final Container EMPTY_CONTAINER = new SimpleContainer(0);

    private final Player player;
    private final AccessoryContainer handler;
    private final AccessoryType type;

    public AccessorySlot(Player player, AccessoryContainer handler, int index, int x, int y, AccessoryType type) {
        super(EMPTY_CONTAINER, index, x, y);
        this.player = player;
        this.handler = handler;
        this.type = type;
    }

    public AccessoryType getType() {
        return type;
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        IAccessory accessory = AccessoryHelper.getBoundAccessory(item);

        if (accessory != null) {
            return handler.isItemValid(player, getContainerSlot(), stack, EquipContext.SLOT_PLACE) && AccessoryHelper.getType(item) == type;
        }

        return false;
    }

    @Override
    public boolean mayPickup(@NonNull Player player) {
        ItemStack stack = getItem();

        if (stack.isEmpty()) {
            return false;
        }

        IAccessory accessory = AccessoryHelper.getBoundAccessory(stack.getItem());
        boolean original = !EnchantmentHelper.hasBindingCurse(stack);

        if (accessory != null) {
            original &= accessory.canUnequip(player, getItem());
        }

        return OhmegaHooks.accessoryCanUnequipEvent(player, getItem(), original);
    }

    @Override
    public @NonNull ItemStack getItem() {
        return handler.getStackInSlot(getContainerSlot());
    }

    @Override
    public void onQuickCraft(@NonNull ItemStack oldStack, @NonNull ItemStack newStack) {}

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(@NonNull ItemStack stack) {
        return getMaxStackSize();
    }

    @Override
    public @NonNull ItemStack remove(int amount) {
        int index = getContainerSlot();
        ItemStack stack = ContainerHelper.removeItem(handler.getStacks(), index, amount);

        if (!ItemStack.isSameItemSameTags(handler.getStackInSlot(index), stack)) {
            handler.doUnequip(player, stack);
            handler.onContentsChanged(index);
        }

        return stack;
    }

    @Override
    public void set(@NonNull ItemStack stack) {
        handler.setStackInSlot(player, getContainerSlot(), stack, EquipContext.SLOT_PLACE);
    }

    @Override
    public boolean allowModification(@NonNull Player player) {
        return true;
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, type.getEmptySlotLocation());
    }
}