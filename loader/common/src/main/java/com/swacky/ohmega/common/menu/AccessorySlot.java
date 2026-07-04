package com.swacky.ohmega.common.menu;

import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
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

// todo: move to api and abstract, creating this as the default impl
public final class AccessorySlot extends Slot {
    private static final Container EMPTY_CONTAINER = new SimpleContainer(0);

    private final Player player;
    private final AccessoryData handler;
    private final AccessoryType type;
    private final int originalX;
    private final int originalY;

    public AccessorySlot(Player player, int index, int x, int y, AccessoryType type) {
        super(EMPTY_CONTAINER, index, x, y);

        this.player = player;
        this.handler = OhmegaDataAttachments.getData(player);
        this.type = type;
        this.originalX = x;
        this.originalY = y;
    }

    public AccessoryType getType() {
        return type;
    }

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

    public void applyOffset(int xo, int yo) {
        this.x = originalX + xo;
        this.y = originalY + yo;
    }

    @Override
    public boolean isActive() {
        if (player.containerMenu instanceof IAccessoryMenu menu) {
            if (!menu.isAccessoryExtensionVisible()) {
                return false;
            }
        }

        if (player.level().isClientSide()) {
            return AccessoryScreens.areExtensionWidgetsVisible();
        }

        return true;
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null) {
            return handler.getEntry(getContainerSlot()).isItemValid(player, stack, EquipContext.SLOT);
        }

        return false;
    }

    @Override
    public boolean mayPickup(@NonNull Player player) {
        ItemStack stack = getItem();

        if (stack.isEmpty()) {
            return true;
        }

        if (EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
            return false;
        }

        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null) {
            return accessory.canUnequip(player, getItem());
        }

        return super.mayPickup(player);
    }

    @Override
    public @NonNull ItemStack getItem() {
        return handler.getEntry(getContainerSlot()).getStack();
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
        return handler.getEntry(getContainerSlot()).remove(player, amount, EquipContext.SLOT);
    }

    @Override
    public void set(@NonNull ItemStack stack) {
        handler.getEntry(getContainerSlot()).setStack(player, stack, getContainerSlot(), EquipContext.SLOT);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean allowModification(@NonNull Player player) {
        return true;
    }

    @Override
    public Identifier getNoItemIcon() {
        return type.getEmptySlotLocation();
    }
}