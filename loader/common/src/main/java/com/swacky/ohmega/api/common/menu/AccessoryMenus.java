package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.common.menu.ServerAccessoryMenuExtension;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Holds methods related to menu extensions that implement correct functionality
 * @apiNote In order for the extension to function properly, you should call:
 * <ul>
 *     <li>{@link #onConstruct(AbstractContainerMenu, Player)}</li>
 *     <li>{@link #onQuickMoveStack(AbstractContainerMenu, Player, int)}</li>
 * </ul>
 */
public final class AccessoryMenus {
    /**
     * Asserts that the passed {@link AbstractContainerMenu} implements {@link IAccessoryMenu}, otherwise {@code throw}s
     * @param menu vanilla menu instance to assert
     * @return the cast {@link IAccessoryMenu}
     */
    public static @NonNull IAccessoryMenu assertImplementation(@NonNull AbstractContainerMenu menu) {
        if (menu instanceof IAccessoryMenu accessoryMenu) {
            return accessoryMenu;
        } else {
            throw new IllegalArgumentException("Menu " + menu.getClass().getCanonicalName() + " does not implement " + IAccessoryMenu.class.getCanonicalName());
        }
    }

    /**
     * Creates a list of pertaining to the accessory extension {@link AccessorySlot}s to (optionally) perform operations on through a callback
     * @param menu parent menu
     * @param owner player which this menu belongs
     * @param consumer a callback to perform an operation for each generated {@link AccessorySlot}, supplying it
     * @return a list of {@link AccessorySlot}s to add with the accessory extension to the menu
     * @apiNote Called by {@link #onConstruct(AbstractContainerMenu, Player)} and will usually not need to be invoked manually
     */
    public static List<AccessorySlot> createSlots(@NonNull AbstractContainerMenu menu, @NonNull Player owner, @Nullable Consumer<AccessorySlot> consumer) {
        AccessoryData data = OhmegaDataAttachments.getData(owner);
        ArrayList<AccessoryDataEntry> entries = data.getEntries();
        int requiredCount = data.size();
        List<AccessorySlot> slots = new ArrayList<>(requiredCount);

        if (owner.level().isClientSide()) {
            AccessoryMenuExtension extension = AccessoryExtensions.getActiveMenuFactory().construct(menu, owner);

            extension.addSlots((index, x, y) -> {
                AccessorySlot slot = new AccessorySlot(
                        owner,
                        index,
                        x,
                        y,
                        entries.get(index).getType());

                slots.add(slot);

                if (consumer != null) {
                    consumer.accept(slot);
                }
            });

            int actualCount = slots.size();

            if (actualCount != requiredCount) {
                throw new IllegalStateException("Slots added by extension '" + extension + "' (" + actualCount + ") differ in length from required " + requiredCount);
            }
        } else {
            for (int i = 0; i < requiredCount; i++) {
                AccessorySlot slot = new AccessorySlot(owner, i, 0, 0, entries.get(i).getType());

                slots.add(slot);

                if (consumer != null) {
                    consumer.accept(slot);
                }
            }
        }

        return slots;
    }

    /**
     * Called by {@link #onConstruct(AbstractContainerMenu, Player)} and will usually not need to be invoked manually
     * @param menu parent menu
     * @param owner player which this menu belongs
     * @param accessoryMenu cast version of the {@code menu} to {@link IAccessoryMenu}
     * @return the menu extension that has been attached to the given {@link IAccessoryMenu}
     */
    public static AccessoryMenuExtension attachExtension(@NonNull AbstractContainerMenu menu, @NonNull Player owner, @NonNull IAccessoryMenu accessoryMenu) {
        AccessoryMenuExtension extension;

        if (owner.level().isClientSide()) {
            extension = AccessoryExtensions.getActiveMenuFactory().construct(menu, owner);
        } else {
            extension = new ServerAccessoryMenuExtension(menu, owner);
        }

        accessoryMenu.setAccessoryExtension(extension);
        return extension;
    }

    /**
     * This must be called at the end of your target menu's constructor to assign the accessory extension and add slots.
     * The logic handled here is split into separate functions above because it may be useful at times to only run parts of the construction
     * @param menu parent menu
     * @param owner player which this menu belongs
     * @apiNote Ohmega calls this in another place internally, do not replicate. It is to fix an annoying edge case
     */
    public static void onConstruct(@NonNull AbstractContainerMenu menu, @NonNull Player owner) {
        attachExtension(menu, owner, assertImplementation(menu)).setSlots(createSlots(menu, owner, menu::addSlot));
    }

    /**
     * Removes all current slots and calls {@link AccessoryMenuExtension#addSlots(AccessoryMenuExtension.SlotAdder)} again.
     * Used internally to rebuild the accessory slots after they have changed, usually via in-game commands
     * @param menu parent menu
     * @param owner player which this menu belongs
     */
    public static void rebuildSlots(@NonNull AbstractContainerMenu menu, @NonNull Player owner) {
        IAccessoryMenu accessoryMenu = assertImplementation(menu);
        List<AccessorySlot> slots = accessoryMenu.getSlots();

        if (slots != null) {
            menu.slots.removeAll(slots);
        }

        AccessoryMenuExtension extension = accessoryMenu.getAccessoryExtension();

        if (extension != null) {
            extension.setSlots(createSlots(menu, owner, menu::addSlot));
        }
    }

    /**
     * Attempts to re-build the accessory slots for the {@link InventoryMenu} and the currently opened menu if the passed {@code entity} is a {@link Player}.
     * @param entity owner of the menu(s),this takes in a {@link LivingEntity}, as to reduce code verbosity
     *               by eliminating the need to assert the entity is a {@link Player} on every invocation
     * @apiNote If successful and on the client, this will also call {@link AccessoryScreens#onRebuildSlots(IAccessoryMenu)}
     */
    public static void tryRebuildSlots(@NonNull LivingEntity entity) {
        if (entity instanceof Player player) {
            AbstractContainerMenu menu = player.inventoryMenu;

            if (menu instanceof IAccessoryMenu) {
                rebuildSlots(menu, player);
            }

            menu = player.containerMenu;

            if (menu instanceof IAccessoryMenu accessoryMenu) {
                rebuildSlots(menu, player);

                if (player.level().isClientSide()) {
                    AccessoryScreens.onRebuildSlots(accessoryMenu);
                }
            }
        }
    }

    /**
     * A version of {@link AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)} that will return the moved {@link ItemStack} if successful,
     * and {@link ItemStack#EMPTY} if nothing has been moved. This allows us to modify the target stack if it has been moved,
     * which is impossible in the normal version as it may return a newly created {@link ItemStack} instead of the one passed as a parameter
     * @param menu menu we are moving in
     * @param stack {@link ItemStack} we are moving
     * @param startIndex lowest index to search
     * @param endIndex highest index to search, exclusive
     * @param reverseDirection move back to front, starting from the end index -1 (to retain exclusivity)
     * @return the {@link ItemStack} that has been moved, or {@link ItemStack#EMPTY} if nothing changed
     */
    public static @NonNull ItemStack tryMoveItemStackTo(@NonNull Player player, @NonNull AbstractContainerMenu menu, @NonNull AccessoryData data, @NonNull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        int i = startIndex;

        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = menu.getSlot(i);
                ItemStack stack0 = slot.getItem();

                if (!stack0.isEmpty()) {
                    ItemStack stack1 = stack.copy();

                    AccessoryDataEntry.doUnequip(player, stack1, EquipContext.SLOT);

                    if (ItemStack.isSameItemSameComponents(stack1, stack0)) {
                        int j = stack0.getCount() + stack.getCount();
                        int maxSize = slot.getMaxStackSize(stack0);

                        if (j <= maxSize) {
                            stack.setCount(0);
                            stack0.setCount(j);
                            slot.setChanged();
                        } else if (stack0.getCount() < maxSize) {
                            stack.shrink(maxSize - stack0.getCount());
                            stack0.setCount(maxSize);
                            slot.setChanged();
                        }
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

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = menu.getSlot(i);
                ItemStack stack1 = slot.getItem();

                if (stack1.isEmpty() && slot.mayPlace(stack)) {
                    stack0 = stack.split(Math.min(stack.getCount(), slot.getMaxStackSize(stack)));

                    slot.setByPlayer(stack0);
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
     * @return {@link ItemStack} after moving
     */
    public static @NonNull ItemStack quickMoveStack(@NonNull AbstractContainerMenu menu, @NonNull Player player, int index) {
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
                AccessoryData data = OhmegaDataAttachments.getData(player);
                int openIndex = data.getFirstOpenSlot(Accessories.getType(player, item));

                if (Accessories.get(item) != null && index > 8 && index < 45 && openIndex >= 0 && menu.getSlot(46 + openIndex).mayPlace(stack0)) { // Inventory -> accessory
                    if (!menu.moveItemStackTo(stack, 46, 52, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (index > 45 && index < 52) { // Accessory -> inventory
                        ItemStack stack1 = tryMoveItemStackTo(player, menu, data, stack, 9, 45, false);

                        if (!stack1.isEmpty()) {
                            AccessoryDataEntry.doUnequip(player, stack1, EquipContext.SLOT);
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
     * @param menu Menu we are moving a stack in, usually supplied by caller {@code this} instance
     * @param player supplied by method override: player which this menu belongs
     * @param index supplied by method override: slot index we are moving from
     * @return {@link ItemStack} after moving, or {@code null} on failing, at which point you should rely on a fallback
     */
    public static @Nullable ItemStack onQuickMoveStack(@NonNull AbstractContainerMenu menu, @NonNull Player player, int index) {
        if (assertImplementation(menu).isAccessoryExtensionVisible()) {
            return quickMoveStack(menu, player, index);
        }

        return null;
    }
}
