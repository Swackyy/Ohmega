package com.swacky.ohmega.common.menu;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class AccessoryInventoryMenu extends AbstractContainerMenu {
    public static final Identifier[] ARMOR_SLOT_TEXTURES = new Identifier[]{InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private final Player player;
    private final CraftingContainer craftMatrix = new TransientCraftingContainer(this, 2, 2);
    private final ResultContainer craftResult = new ResultContainer();
    public final int renderSlots;
    public final int renderColumns;
    public final int slotsAvailable;

    public AccessoryInventoryMenu(int id, Inventory inv) {
        super(OhmegaMenus.getAccessoryMenu(), id);
        this.player = inv.player;
        int x;

        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

        if (player.level().isClientSide() && OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
            x = 2 + 4 * 2 + 18 * (int) Math.min(Math.ceil((double) slotTypes.size() / Math.min(OhmegaConfig.Client.maxColumnRenderSlots(), OhmegaConfig.Client.maxColumnSlots())), OhmegaConfig.Client.maxColumns());
        } else {
            x = 0;
        }

        addResultSlot(player, x + 154, 28);
        addCraftingGridSlots(2, 2, x + 98, 18);

        for (int i = 0; i < 4; i++) { // Armour Slots
            EquipmentSlot equipmentSlotType = VALID_EQUIPMENT_SLOTS[i];
            addSlot(new ArmorSlot(inv, player, equipmentSlotType, 39 - i, x + 8, 8 + i * 18, ARMOR_SLOT_TEXTURES[equipmentSlotType.getIndex()]));
        }

        addStandardInventorySlots(inv, x + 8, 84);
        addSlot(new OffhandSlot(inv, 40, x + 77, 62)); // Offhand Slot

        AccessoryContainer container = AccessoryHelper.getContainer(player);

        if (!player.level().isClientSide()) {
            renderSlots = 0;
            renderColumns = 0;
            slotsAvailable = 0;

            for (int i = 0; i < slotTypes.size(); i++) {
                // Position does not matter (only in rare cases) on server
                addSlot(new AccessorySlot(player, container, i, 0, 0, slotTypes.get(i)));
            }
        } else {
            renderSlots = Math.min(OhmegaConfig.Client.maxColumnSlots(), OhmegaConfig.Client.maxColumnRenderSlots());
            renderColumns = (int) Math.min(Math.ceil((double) slotTypes.size() / renderSlots), OhmegaConfig.Client.maxColumns());
            slotsAvailable = Math.min(renderColumns * renderSlots, slotTypes.size());

            if (OhmegaConfig.Client.side() == OhmegaConfig.Client.Service.Side.LEFT) {
                // 2px buffer from inv, 4 px buffer on both sides, indexes columns width, 1px to align
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
                    addSlot(new AccessorySlot(player, container, index, x + 4 + 18 * i, 25 + j * 18, slotTypes.get(index++)));

                    if (++slotsCreatedCurrentColumn >= renderSlots) {
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

    private void addResultSlot(Player pPlayer, int x, int y) {
        this.addSlot(new ResultSlot(pPlayer, craftMatrix, craftResult, 0, x, y));
    }

    private void addCraftingGridSlots(int width, int height, int x, int y) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.addSlot(new Slot(craftMatrix, j + i * width, x + j * 18, y + i * 18));
            }
        }
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }

    @Override
    public void slotsChanged(@NonNull Container container) {
        if (player.level() instanceof ServerLevel level) {
            CraftingMenu.slotChangedCraftingGrid(this, level, player, craftMatrix, craftResult, null);
        }

        super.slotsChanged(container);
    }

    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);
        craftResult.clearContent();

        if (!player.level().isClientSide()) {
            clearContainer(player, craftMatrix);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(@NonNull ItemStack stack, Slot slot) {
        return slot.container != craftResult && super.canTakeItemForPickAll(stack, slot);
    }

    private ItemStack tryMoveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        int i = startIndex;

        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
                Slot slot = slots.get(i);
                ItemStack stack0 = slot.getItem();

                if (!stack0.isEmpty() && ItemStack.isSameItemSameComponents(stack, stack0)) {
                    int j = stack0.getCount() + stack.getCount();
                    if (j <= stack.getMaxStackSize()) {
                        stack.setCount(0);
                        stack0.setCount(j);
                        slot.setChanged();
                    } else if (stack0.getCount() < stack.getMaxStackSize()) {
                        stack.shrink(stack.getMaxStackSize() - stack0.getCount());
                        stack0.setCount(stack.getMaxStackSize());
                        slot.setChanged();
                    }
                }

                if (reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        ItemStack stack0 = ItemStack.EMPTY;

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(reverseDirection ? i >= startIndex : i < endIndex) {
                Slot slot = slots.get(i);
                ItemStack stack1 = slot.getItem();

                if (stack1.isEmpty() && slot.mayPlace(stack)) {
                    if (stack.getCount() > slot.getMaxStackSize()) {
                        stack0 = stack.split(slot.getMaxStackSize());
                    } else {
                        stack0 = stack.split(stack.getCount());
                    }

                    slot.set(stack0);
                    slot.setChanged();
                    break;
                }

                if (reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        return stack0;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack stack0 = stack.copy();

            if (index == 0) {
                if (!moveItemStackTo(stack, 9, 45, true)) { // Crafting result -> inventory
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, stack0);
            } else if (index >= 1 && index < 5) {
                if (!moveItemStackTo(stack, 9, 45, false)) { // Crafting grid -> inventory
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!moveItemStackTo(stack, 9, 45, false)) { // Armour -> inventory
                    return ItemStack.EMPTY;
                }
            } else {
                Item item = stack.getItem();
                AccessoryType type = AccessoryHelper.getType(item);
                int openIndex = AccessoryHelper.getFirstOpenSlot(player, type);
                IAccessory accessory = AccessoryHelper.getAccessory(item);
                Slot slot0 = getSlot(46 + openIndex);

                if (accessory != null && index > 8 && index < 45 && openIndex != -1 && slot0.mayPlace(stack0)) { // Inventory -> accessory
                    stack.consume(1, player);
                    stack0.setCount(1);
                    slot0.set(stack0);
                } else {
                    if (index > 45 && index < 52) {
                        ItemStack stack1 = tryMoveItemStackTo(stack, 9, 45, false);

                        if (!stack1.isEmpty()) { // Accessory -> inventory
                            AccessoryHelper.getContainer(player).doUnequip(player, stack1);
                            slot.setChanged();
                            return ItemStack.EMPTY;
                        }
                    } else {
                        EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(stack0);

                        if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !this.slots.get(8 - equipmentSlot.getIndex()).hasItem() && player.isEquippableInSlot(stack, equipmentSlot)) {
                            int i = 8 - equipmentSlot.getIndex();

                            if (!moveItemStackTo(stack, i, i + 1, false)) { // Item -> armour
                                return ItemStack.EMPTY;
                            }
                        } else if (equipmentSlot == EquipmentSlot.OFFHAND && !slots.get(45).hasItem()) {
                            if (!moveItemStackTo(stack, 45, 46, false)) { // Item -> offhand
                                return ItemStack.EMPTY;
                            }
                        } else if (index >= 9 && index < 36) {
                            if (!moveItemStackTo(stack, 36, 45, false)) { // Extended inventory -> higher extended inventory
                                return ItemStack.EMPTY;
                            }
                        } else if (index > 35 && index < 45) {
                            if (!moveItemStackTo(stack, 9, 36, false)) { // Hotbar -> extended inventory
                                return ItemStack.EMPTY;
                            }
                        } else if (!moveItemStackTo(stack, 9, 45, false)) { // Etc -> inventory
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY, stack0);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == stack0.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);

            if (index == 0) {
                player.drop(stack, false);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> items, @NonNull ItemStack carried) {
        for (int i = 0; i < Math.min(slots.size(), items.size()); i++) {
            Slot slot = getSlot(i);
            ItemStack stack = items.get(i);

            if (!ItemStack.matches(slot.getItem(), stack)) {
                slot.set(stack);
            }
        }

        setCarried(carried);
        this.stateId = stateId;
    }

    public Player getPlayer() {
        return player;
    }

    private static class OffhandSlot extends Slot {
        public OffhandSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public Identifier getNoItemIcon() {
            return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
        }
    }
}
