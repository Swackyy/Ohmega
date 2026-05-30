package com.swacky.ohmega.api.common.menu;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.client.ui.AccessoryUIs;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.item.Accessory;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.common.menu.ServerAccessoryMenuExtension;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds methods related to menu extensions that implement correct functionality
 * <p>
 * In order for the extension to function properly, you should call:
 * <ul>
 *     <li>{@link #onConstruct(AbstractContainerMenu, Player)}</li>
 *     <li>{@link #onQuickMoveStack(AbstractContainerMenu, Player, int)}</li>
 * </ul>
 */
public final class AccessoryMenus {
    /**
     * This must be called at the end of your target menu's constructor to assign the accessory extension and add slots
     * <p>
     * Ohmega calls this in another place internally, do not replicate. It is to fix an annoying edge case
     * @param menu parent menu
     * @param owner player which this menu belongs
     */
    public static void onConstruct(AbstractContainerMenu menu, Player owner) {
        if (menu instanceof IAccessoryMenu accessoryMenu) {
            ImmutableList<AccessoryType> types = AccessoryHelper.getSlotTypes();
            int requiredCount = types.size();

            if (owner.level().isClientSide()) {
                AccessoryMenuExtension extension = AccessoryUIs.getActiveMenuFactory().construct(menu, owner);
                List<AccessorySlot> slots = new ArrayList<>(requiredCount);

                accessoryMenu.setAccessoryExtension(extension);

                extension.addSlots((index, x, y) -> {
                    AccessorySlot slot = new AccessorySlot(
                            owner,
                            index,
                            x,
                            y,
                            types.get(index));

                    menu.addSlot(slot);
                    slots.add(slot);
                });

                int actualCount = slots.size();

                if (actualCount != requiredCount) {
                    throw new IllegalStateException("Slots added by extension '" + extension + "' (" + actualCount + ") differ in length from required " + requiredCount);
                }

                extension.setSlots(slots);
            } else {
                accessoryMenu.setAccessoryExtension(new ServerAccessoryMenuExtension(menu, owner));

                for (int i = 0; i < requiredCount; i++) {
                    menu.addSlot(new AccessorySlot(owner, i, 0, 0, types.get(i)));
                }
            }
        } else {
            throw new IllegalArgumentException("Menu " + menu.getClass().getCanonicalName() + " does not implement " + IAccessoryMenu.class.getCanonicalName());
        }
    }

    /**
     * A version of {@link AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)} that will return the moved {@link ItemStack} if successful,
     * and {@link ItemStack#EMPTY} if nothing has been moved. This allows us to modify the target stack if it has been moved,
     * which is impossible in the normal version as it may return a newly created {@link ItemStack} instead of the one passed as a parameter
     * @param menu menu we are moving in
     * @param stack {@link ItemStack} we are moving
     * @param startIndex lowest index to search, inclusive
     * @param endIndex highest index to search, exclusive
     * @param reverseDirection move back to front, starting from the end index -1 (to retain exclusivity)
     * @return the {@link ItemStack} that has been moved, or {@link ItemStack#EMPTY} if nothing changed
     */
    public static @NonNull ItemStack tryMoveItemStackTo(@NonNull AbstractContainerMenu menu, @NonNull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        int i = startIndex;

        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
                Slot slot = menu.getSlot(i);
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

            while (reverseDirection ? i >= startIndex : i < endIndex) {
                Slot slot = menu.getSlot(i);
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

    // todo: try splitting to allow for injection points possibly? would improve compatibility
    /**
     * The default {@code quickMoveStack} implementation, supporting both when the menu is visible and hidden
     * @param menu supplied by caller {@code this} instance usually: menu we are moving a stack in
     * @param player supplied by method override: player which this menu belongs
     * @param index supplied by method override: slot index we are moving from
     * @param considerExtensionSlots whether we should actually try moving to/from accessory slots
     * @return {@link ItemStack} after moving
     */
    @SuppressWarnings("SameReturnValue")
    public static @NonNull ItemStack quickMoveStack(@NonNull AbstractContainerMenu menu, @NonNull Player player, int index, boolean considerExtensionSlots) {
        Slot slot = menu.getSlot(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack stack0 = stack.copy();

            if (index == 0) {
                if (!menu.moveItemStackTo(stack, 9, 45, true)) { // Crafting result -> inventory
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, stack0);
            } else if (index >= 1 && index < 5) {
                if (!menu.moveItemStackTo(stack, 9, 45, false)) { // Crafting grid -> inventory
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!menu.moveItemStackTo(stack, 9, 45, false)) { // Armour -> inventory
                    return ItemStack.EMPTY;
                }
            } else {
                Item item = stack.getItem();
                AccessoryType type = AccessoryHelper.getType(item);
                int openIndex = AccessoryHelper.getFirstOpenSlot(player, type);
                Accessory accessory = Accessories.get(item);
                Slot slot0 = menu.getSlot(46 + openIndex);

                if (considerExtensionSlots && accessory != null && index > 8 && index < 45 && openIndex >= 0 && slot0.mayPlace(stack0)) { // Inventory -> accessory
                    if (!menu.moveItemStackTo(stack, 46, 52, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (considerExtensionSlots && index > 45 && index < 52) { // Accessory -> inventory
                        ItemStack stack1 = tryMoveItemStackTo(menu, stack, 9, 45, false);

                        if (!stack1.isEmpty()) {
                            AccessoryHelper.getData(player).doUnequip(player, stack1);
                            slot.setChanged();
                        }
                    } else {
                        EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(stack0);

                        if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !menu.getSlot(8 - equipmentSlot.getIndex()).hasItem() && player.isEquippableInSlot(stack, equipmentSlot)) {
                            int i = 8 - equipmentSlot.getIndex();

                            if (!menu.moveItemStackTo(stack, i, i + 1, false)) { // Item -> armour
                                return ItemStack.EMPTY;
                            }
                        } else if (equipmentSlot == EquipmentSlot.OFFHAND && !menu.getSlot(45).hasItem()) {
                            if (!menu.moveItemStackTo(stack, 45, 46, false)) { // Item -> offhand
                                return ItemStack.EMPTY;
                            }
                        } else if (index >= 9 && index < 36) {
                            if (!menu.moveItemStackTo(stack, 36, 45, false)) { // Extended inventory -> higher extended inventory
                                return ItemStack.EMPTY;
                            }
                        } else if (index > 35 && index < 45) {
                            if (!menu.moveItemStackTo(stack, 9, 36, false)) { // Hotbar -> extended inventory
                                return ItemStack.EMPTY;
                            }
                        } else if (!menu.moveItemStackTo(stack, 9, 45, false)) { // Etc -> inventory
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

    /**
     * This should be the return value of your target menu's {@link AbstractContainerMenu#quickMoveStack(Player, int)}.
     * You could always use a custom method however the built-in one handles every case needed for Ohmega to work well
     * See {@link InventoryMenu#quickMoveStack(Player, int)} for an example of a fallback
     * @param menu supplied by caller {@code this} instance usually: menu we are moving a stack in
     * @param player supplied by method override: player which this menu belongs
     * @param index supplied by method override: slot index we are moving from
     * @return {@link ItemStack} after moving, or {@code null} on failing, at which point you should rely on a fallback
     */
    public static @NonNull ItemStack onQuickMoveStack(AbstractContainerMenu menu, Player player, int index) {
        if (menu instanceof IAccessoryMenu accessoryMenu) {
            return quickMoveStack(menu, player, index, accessoryMenu.isAccessoryExtensionVisible());
        } else {
            throw new IllegalArgumentException("Menu " + menu + " does not implement " + IAccessoryMenu.class);
        }
    }
}
