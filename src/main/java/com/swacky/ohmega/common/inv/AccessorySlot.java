package com.swacky.ohmega.common.inv;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryType;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AccessorySlot extends SlotItemHandler {
    protected final Player player;
    protected final int slot;
    protected final AccessoryType type;
    public AccessorySlot(Player player, IItemHandler handler, int index, int x, int y, AccessoryType type) {
        super(handler, index, x, y);
        this.player = player;
        this.slot = index;
        this.type = type;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if(stack.getItem() instanceof IAccessory accessory) {
            return ((AccessoryContainer) getItemHandler()).isValid(stack) && accessory.getType() == this.type;
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(Player player) {
        return !getItem().isEmpty() && getItem().getCapability(Ohmega.ACCESSORY_ITEM).orElseThrow(NullPointerException::new).canUnequip(player);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        if(!hasItem() && stack.getCapability(Ohmega.ACCESSORY_ITEM).isPresent()) {
            stack.getCapability(Ohmega.ACCESSORY_ITEM, null).ifPresent(acc -> {
                acc.onUnequip(player, stack);
                stack.getOrCreateTag().putInt("slot", -1);
                AccessoryHelper.addActiveTag(stack, false);
            });
        }
        super.onTake(player, stack);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        if (hasItem() && !ItemStack.isSame(stack, getItem()) && getItem().getCapability(Ohmega.ACCESSORY_ITEM).isPresent()) {
            getItem().getCapability(Ohmega.ACCESSORY_ITEM).ifPresent(acc -> {
                acc.onUnequip(player, stack);
                stack.getOrCreateTag().putInt("slot", -1);
                AccessoryHelper.addActiveTag(stack, false);
            });
        }

        ItemStack old = getItem().copy();
        super.set(stack);

        if (hasItem() && !ItemStack.isSame(old, getItem()) && getItem().getCapability(Ohmega.ACCESSORY_ITEM).isPresent()) {
            getItem().getCapability(Ohmega.ACCESSORY_ITEM).ifPresent(acc -> {
                stack.getOrCreateTag().putInt("slot", slot);
                AccessoryHelper.addActiveTag(stack, true);
                acc.onEquip(player, stack);
            });
        }
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(Ohmega.MODID, "gui/accessory_slot_" + this.type.getIdentifier()));
    }

    public AccessoryType getType() {
        return type;
    }
}