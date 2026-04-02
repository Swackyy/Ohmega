package com.swacky.ohmega.common.menu;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
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
        IAccessory accessory = AccessoryHelper.getAccessory(item);

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

        IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());
        boolean original = !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);

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
        return handler.remove(player, getContainerSlot(), amount);
    }

    @Override
    public void set(@NonNull ItemStack stack) {
        handler.setStack(player, getContainerSlot(), stack, EquipContext.SLOT_PLACE);
    }

    @Override
    public void setChanged() {
        handler.setChanged(getContainerSlot());
    }

    @Override
    public boolean allowModification(@NonNull Player player) {
        return true;
    }

    @Override
    public Identifier getNoItemIcon() {
        return type.getEmptySlotLocation();
    }
}