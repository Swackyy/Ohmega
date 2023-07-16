package com.swacky.ohmega.common.inv;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.AccessoryType;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
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
import java.util.ArrayList;

import static com.swacky.ohmega.api.AccessoryType.*;

public class AccessoryInventoryMenu extends AbstractContainerMenu {
    protected SimpleContainerData data;
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

        for (int index = 0; index < 6; index++) { // Accessory Slots
            SLOTS[index] = (AccessorySlot) this.addSlot(new AccessorySlot(inv.player, accessories, index, 183, 25 + index * 18, SLOT_TYPES[index]));
        }

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
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public @NotNull AccessorySlot getAccessorySlot(int index) {
        return SLOTS[index];
    }

    public @NotNull AccessorySlot[] getAccessorySlot() {
        return SLOTS;
    }

    public @NotNull ArrayList<AccessorySlot> getAccessorySlot(AccessoryType type) {
        ArrayList<AccessorySlot> slotsOut = new ArrayList<>();
        for(AccessorySlot slot : SLOTS) {
            if(slot.getType() == type) slotsOut.add(slot);
        }
        return slotsOut;
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
