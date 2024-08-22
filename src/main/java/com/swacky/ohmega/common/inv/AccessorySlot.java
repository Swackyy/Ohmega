package com.swacky.ohmega.common.inv;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryType;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.events.AccessoryEquipEvent;
import com.swacky.ohmega.api.events.AccessoryUnequipEvent;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class AccessorySlot extends SlotItemHandler {
    protected final Player player;
    protected final int slot;
    protected final AccessoryType type;

    private static final ResourceLocation SLOT_NORMAL = ResourceLocation.fromNamespaceAndPath(Ohmega.MODID, "item/accessory_slot_normal");
    private static final ResourceLocation SLOT_UTILITY = ResourceLocation.fromNamespaceAndPath(Ohmega.MODID, "item/accessory_slot_utility");
    private static final ResourceLocation SLOT_SPECIAL = ResourceLocation.fromNamespaceAndPath(Ohmega.MODID, "item/accessory_slot_special");
    public static final ResourceLocation[] SLOTS = new ResourceLocation[]{SLOT_NORMAL, SLOT_UTILITY, SLOT_SPECIAL};

    public AccessorySlot(Player player, IItemHandler handler, int index, int x, int y, AccessoryType type) {
        super(handler, index, x, y);
        this.player = player;
        this.slot = index;
        this.type = type;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if(acc != null) {
            return ((AccessoryContainer) getItemHandler()).isValid(stack) && acc.getType() == this.type && AccessoryHelper.isExclusiveType(this.player, stack);
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(Player player) {
        boolean original = true;
        IAccessory acc = AccessoryHelper.getBoundAccessory(getItem().getItem());
        if(acc != null) {
            original = acc.canUnequip(player, getItem());
        }
        return !getItem().isEmpty() && OhmegaHooks.accessoryCanUnequipEvent(player, getItem(), original).getReturnValue();
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if (!hasItem() && acc != null) {
            IAccessory.ModifierBuilder builder = IAccessory.ModifierBuilder.deserialize(stack);
            AccessoryHelper.changeModifiers(player, builder.getModifiers(), false);

            AccessoryUnequipEvent event = OhmegaHooks.accessoryUnequipEvent(this.player, stack);
            if (!event.isCanceled()) {
                acc.onUnequip(this.player, stack);
            }
            AccessoryHelper._internalTag(stack).putInt("slot", -1);
            AccessoryHelper.setActive(this.player, stack, false);
        }
        super.onTake(player, stack);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
        if (hasItem() && !ItemStack.isSameItem(stack, getItem()) && acc != null) {
            AccessoryHelper.changeModifiers(this.player, IAccessory.ModifierBuilder.deserialize(stack).getModifiers(), false);

            AccessoryUnequipEvent event = OhmegaHooks.accessoryUnequipEvent(this.player, stack);
            if (!event.isCanceled()) {
                acc.onUnequip(this.player, stack);
            }

            AccessoryHelper._internalTag(stack).putInt("slot", -1);
            AccessoryHelper.setActive(this.player, stack, false);
            this.setChanged();
        }

        ItemStack old = getItem().copy();
        super.set(stack);

        if (hasItem() && !ItemStack.isSameItem(old, getItem()) && acc != null) {
            AccessoryHelper._internalTag(stack).putInt("slot", this.slot);
            AccessoryHelper.setActive(this.player, stack, true);

            AccessoryHelper.changeModifiers(this.player, IAccessory.ModifierBuilder.deserialize(stack).getModifiers(), true);

            AccessoryEquipEvent event = OhmegaHooks.accessoryEquipEvent(this.player, stack);
            if (!event.isCanceled()) {
                acc.onEquip(this.player, stack);
            }
            this.setChanged();
        }
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, SLOTS[this.type.ordinal()]);
    }
}