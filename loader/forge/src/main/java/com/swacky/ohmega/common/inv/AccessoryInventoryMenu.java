package com.swacky.ohmega.common.inv;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AccessoryInventoryMenu extends AbstractContainerMenu {
    public static final Identifier[] ARMOR_SLOT_TEXTURES = new Identifier[]{InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    protected static AccessorySlot[] accSlots;

    private final Player player;
    protected AccessoryContainer accessories;
    private final CraftingContainer craftMatrix = new TransientCraftingContainer(this, 2, 2);
    private final ResultContainer craftResult = new ResultContainer();

    public AccessoryInventoryMenu(int id, Inventory inv) {
        super(OhmegaMenus.ACCESSORY_INVENTORY.get(), id);

        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();
        AccessoryInventoryMenu.accSlots = new AccessorySlot[slotTypes.size()];

        this.player = inv.player;
        this.accessories = AccessoryHelper.getContainer(inv.player).orElseThrow();

        int x;
        if (inv.player.level().isClientSide() && OhmegaConfig.CONFIG_CLIENT.side.get() == OhmegaConfig.Side.LEFT) {
            x = 2 + 4 * 2 + 18 * (int) Math.min(Math.ceil((double) slotTypes.size() / Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get(), OhmegaConfig.CONFIG_CLIENT.maxColumnSlots.get())), OhmegaConfig.CONFIG_CLIENT.maxColumns.get());
        } else {
            x = 0;
        }

        this.addSlot(new ResultSlot(inv.player, this.craftMatrix, this.craftResult, 0, x + 154, 28));

        for (int i = 0; i < 2; ++i) { // Crafting Matrix Slots
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 2, x + 98 + j * 18, 18 + i * 18));
            }
        }

        for (int i = 0; i < 4; ++i) { // Armour Slots
            EquipmentSlot equipmentSlotType = VALID_EQUIPMENT_SLOTS[i];
            this.addSlot(new ArmorSlot(inv, this.player, equipmentSlotType, 36 + (3 - i), x + 8, 8 + i * 18, ARMOR_SLOT_TEXTURES[equipmentSlotType.getIndex()]));
        }

        for (int i = 0; i < 3; ++i) { // Inventory Slots
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + (i + 1) * 9, x + 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) { // Hotbar Slots
            this.addSlot(new Slot(inv, i, x + 8 + i * 18, 142));
        }

        this.addSlot(new OffhandSlot(inv, 40, x + 77, 62)); // Offhand Slot

        if (!inv.player.level().isClientSide()) {
            for (int i = 0; i < slotTypes.size(); i++) {
                // Position does not matter (only in rare cases) on server
                AccessoryInventoryMenu.accSlots[i] = (AccessorySlot) this.addSlot(new AccessorySlot(inv.player, this.accessories, i, 0, 0, slotTypes.get(i)));
            }
        } else {
            final int renderSlots = Math.min(OhmegaConfig.CONFIG_CLIENT.maxColumnSlots.get(), OhmegaConfig.CONFIG_CLIENT.maxColumnRenderSlots.get());
            final int renderColumns = (int) Math.min(Math.ceil((double) slotTypes.size() / renderSlots), OhmegaConfig.CONFIG_CLIENT.maxColumns.get());
            final int slotsAvailable = Math.min(renderColumns * renderSlots, slotTypes.size());

            if (OhmegaConfig.CONFIG_CLIENT.side.get() == OhmegaConfig.Side.LEFT) {
                // 2px buffer from inv, 4 px buffer on both sides, slots columns width, 1px to align
                x += - 2 - 4 * 2 - 18 * renderColumns + 1;
            } else {
                // Default, 2px buffer from inv, 2px to align
                x += 175 + 2 + 2;
            }

            boolean stop = false;
            int index = 0;
            for (int i = 0; i < renderColumns; i++) {
                if (stop) {
                    break;
                }

                int slotsCreatedCurrentColumn = 0;
                for (int j = 0; true; j++) {
                    AccessoryInventoryMenu.accSlots[j] = (AccessorySlot) this.addSlot(new AccessorySlot(inv.player, this.accessories, index, x + 4 + 18 * i, 25 + j * 18, slotTypes.get(index)));
                    index++;
                    slotsCreatedCurrentColumn++;

                    if (slotsCreatedCurrentColumn >= renderSlots) {
                        break;
                    }

                    if (index >= slotsAvailable) {
                        stop = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        if (this.player.level() instanceof ServerLevel level) {
            CraftingMenu.slotChangedCraftingGrid(this, level, this.player, this.craftMatrix, this.craftResult, null);
        }

        super.slotsChanged(container);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.craftResult.clearContent();

        if (!player.level().isClientSide()) {
            this.clearContainer(player, this.craftMatrix);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.container != this.craftResult && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack0 = slot.getItem();
            stack = stack0.copy();
            EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(stack);
            if (index == 0) {
                if (!this.moveItemStackTo(stack0, 9, 45, true)) { // Crafting result out
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack0, stack);
            } else if (index >= 1 && index < 5) {
                if (!this.moveItemStackTo(stack0, 9, 45, false)) { // Crafting out
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.moveItemStackTo(stack0, 9, 45, false)) { // Armour out
                    return ItemStack.EMPTY;
                }
            } else {
                Item item = stack0.getItem();
                IAccessory acc = AccessoryHelper.getBoundAccessory(item);
                AccessoryType type = AccessoryHelper.getType(item);
                if (acc != null && index > 8 && index < 45 && AccessoryHelper.getFirstOpenSlot(player, type) != -1 && getSlot(46 + AccessoryHelper.getFirstOpenSlot(player, type)).mayPlace(stack)) { // Accessory in
                    int accSlot = AccessoryHelper.getFirstOpenSlot(player, type);
                    stack0.shrink(1);
                    stack.setCount(1);
                    getSlot(46 + accSlot).set(stack);
                } else if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !this.slots.get(8 - equipmentSlot.getIndex()).hasItem()) {
                    int i = 8 - equipmentSlot.getIndex();
                    if (!this.moveItemStackTo(stack0, i, i + 1, false)) { // Armour in
                        return ItemStack.EMPTY;
                    }
                } else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasItem()) {
                    if (!this.moveItemStackTo(stack0, 45, 46, false)) { // Offhand in
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 9 && index < 36) {
                    if (!this.moveItemStackTo(stack0, 36, 45, false)) { // Top part of inv in
                        return ItemStack.EMPTY;
                    }
                } else if (index > 35 && index < 45) {
                    if (!this.moveItemStackTo(stack0, 9, 36, false)) { // Hotbar out
                        return ItemStack.EMPTY;
                    }
                } else if (index > 45 && index < 52 && acc != null) {
                    AccessoryHelper.changeModifiers(player, AccessoryHelper.getModifiers(stack0).getPassive(), false);

                    if (!OhmegaHooks.accessoryUnequipEvent(this.player, stack0)) {
                        acc.onUnequip(this.player, stack0);
                    }

                    AccessoryHelper.setSlot(stack0, -1);
                    if (this.moveItemStackTo(stack0, 9, 45, false)) { // Accessory out
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack0, 9, 45, false)) { // Etc into the top part of inv
                    return ItemStack.EMPTY;
                }
            }

            if (stack0.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY, stack);
            } else {
                slot.setChanged();
            }

            if (stack0.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack0);
            if (index == 0) {
                player.drop(stack0, false);
            }
        }

        return stack;
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> items, @NotNull ItemStack carried) {
        for (int i = 0; i < Math.min(this.slots.size(), items.size()); i++) {
            Slot slot = this.getSlot(i);
            ItemStack stack = items.get(i);
            if (!ItemStack.matches(slot.getItem(), stack)) {
                this.getSlot(i).set(stack);
            }
        }

        this.setCarried(carried);
        this.stateId = stateId;
    }

    private static class OffhandSlot extends Slot {
        public OffhandSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return super.mayPlace(stack);
        }

        @Override
        public Identifier getNoItemIcon() {
            return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
        }
    }
}
