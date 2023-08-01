package com.swacky.ohmega.common.inv;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryType;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.swacky.ohmega.api.AccessoryType.*;

public class AccessoryInventoryMenu extends AbstractContainerMenu {
    protected AccessoryContainer accessories;
    protected static final AccessorySlot[] SLOTS = new AccessorySlot[6];
    protected static final AccessoryType[] SLOT_TYPES = new AccessoryType[]{NORMAL, NORMAL, NORMAL, UTILITY, UTILITY, SPECIAL};
    public static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final CraftingContainer craftMatrix = new CraftingContainer(this, 2, 2);
    private final ResultContainer craftResult = new ResultContainer();
    private final Player player;

    public AccessoryInventoryMenu(int id, Inventory inv) {
        super(ModMenus.ACCESSORY_INVENTORY.get(), id);
        this.player = inv.player;

        this.accessories = inv.player.getCapability(Ohmega.ACCESSORIES).orElseThrow(NullPointerException::new);

        this.addSlot(new ResultSlot(inv.player, this.craftMatrix, this.craftResult, 0, 155, 29));

        for (int i = 0; i < 2; ++i) { // Crafting Matrix Slots
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        for (int k = 0; k < 4; ++k) { // Armour Slots
            var equipmentSlotType = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new ArmorSlot(inv, 36 + (3 - k), 8, 8 + k * 18, equipmentSlotType, this.player));
        }

        for (int l = 0; l < 3; ++l) { // Inventory Slots
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inv, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) { // Hotbar Slots
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 142));
        }

        this.addSlot(new OffhandSlot(inv, 40, 77, 62)); // Offhand Slot

        for (int index = 0; index < 6; index++) { // Accessory Slots
            SLOTS[index] = (AccessorySlot) this.addSlot(new AccessorySlot(inv.player, accessories, index, 183, 25 + index * 18, SLOT_TYPES[index]));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void slotsChanged(@Nonnull Container container) {
        try {
            Method onCraftChange = ObfuscationReflectionHelper.findMethod(CraftingMenu.class, "m_150546_", AbstractContainerMenu.class, Level.class, Player.class, CraftingContainer.class, ResultContainer.class);
            onCraftChange.invoke(null, this, this.player.level, this.player, this.craftMatrix, this.craftResult);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        this.craftResult.clearContent();

        if (!player.level.isClientSide) {
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
            EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(stack);
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
            } else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(8 - equipmentSlot.getIndex()).hasItem()) {
                int i = 8 - equipmentSlot.getIndex();
                if (!this.moveItemStackTo(stack0, i, i + 1, false)) { // Armour in
                    return ItemStack.EMPTY;
                }
            } else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasItem()) {
                if (!this.moveItemStackTo(stack0, 45, 46, false)) { // Offhand in
                    return ItemStack.EMPTY;
                }
            } else if (stack0.getItem() instanceof IAccessory acc && index > 8 && index < 45 && AccessoryHelper.getFirstOpenSlot(player, acc.getType()) != -1 && getSlot(46 + AccessoryHelper.getFirstOpenSlot(player, acc.getType())).mayPlace(stack)) { // Accessory in
                int accSlot = AccessoryHelper.getFirstOpenSlot(player, acc.getType());
                stack0.shrink(1);
                stack.setCount(1);
                getSlot(46 + accSlot).set(stack);
            } else if (index >= 9 && index < 36) {
                if (!this.moveItemStackTo(stack0, 36, 45, false)) { // Top part of inv in
                    return ItemStack.EMPTY;
                }
            } else if (index > 35 && index < 45) {
                if (!this.moveItemStackTo(stack0, 9, 36, false)) { // Hotbar out
                    return ItemStack.EMPTY;
                }
            } else if (index > 45 && index < 52 && stack0.getItem() instanceof IAccessory) {
                stack0.getOrCreateTag().putInt("slot", -1);
                AccessoryHelper.addActiveTag(stack0, false);
                if (this.moveItemStackTo(stack0, 9, 45, false)) { // Accessory out
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack0, 9, 45, false)) { // Etc into top part of inv
                return ItemStack.EMPTY;
            }

            if (stack0.isEmpty()) {
                slot.set(ItemStack.EMPTY);
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

    private static class ArmorSlot extends Slot {
        private final EquipmentSlot slotType;
        private final Player player;

        public ArmorSlot(Container container, int index, int x, int y, EquipmentSlot slotType, Player player) {
            super(container, index, x, y);
            this.slotType = slotType;
            this.player = player;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.canEquip(this.slotType, this.player);
        }

        @Override
        public boolean mayPickup(@Nonnull Player player) {
            ItemStack stack = this.getItem();
            return (stack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(stack)) && super.mayPickup(player);
        }

        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, AccessoryInventoryMenu.ARMOR_SLOT_TEXTURES[slotType.getIndex()]);
        }
    }

    private static class OffhandSlot extends Slot {
        public OffhandSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return super.mayPlace(stack);
        }

        @Nullable
        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
        }
    }
}
