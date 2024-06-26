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
            return ((AccessoryContainer) getItemHandler()).isValid(stack) && accessory.getType() == this.type && AccessoryHelper.isExclusiveType(this.player, stack);
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
        if(getItem().getItem() instanceof IAccessory acc) {
            original = acc.canUnequip(player, getItem());
        }
        return !getItem().isEmpty() && OhmegaHooks.accessoryCanUnequipEvent(player, getItem(), original).getReturnValue();
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        if (!hasItem() && stack.getItem() instanceof IAccessory acc) {
            IAccessory.ModifierBuilder builder = IAccessory.ModifierBuilder.deserialize(stack);
            this.player.getAttributes().removeAttributeModifiers(builder.getModifiers());

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
        if (hasItem() && !ItemStack.isSame(stack, getItem()) && getItem().getItem() instanceof IAccessory acc) {
            IAccessory.ModifierBuilder builder = IAccessory.ModifierBuilder.deserialize(stack);
            this.player.getAttributes().removeAttributeModifiers(builder.getModifiers());

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

        if (hasItem() && !ItemStack.isSame(old, getItem()) && getItem().getItem() instanceof IAccessory acc) {
            AccessoryHelper._internalTag(stack).putInt("slot", this.slot);
            AccessoryHelper.setActive(this.player, stack, true);

            IAccessory.ModifierBuilder builder = IAccessory.ModifierBuilder.deserialize(stack);
            this.player.getAttributes().addTransientAttributeModifiers(builder.getModifiers());

            AccessoryEquipEvent event = OhmegaHooks.accessoryEquipEvent(this.player, stack);
            if (!event.isCanceled()) {
                acc.onEquip(this.player, stack);
            }
            this.setChanged();
        }
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(Ohmega.MODID, "gui/accessory_slot_" + this.type.getIdentifier()));
    }
}