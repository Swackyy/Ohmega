package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.item.Accessory;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A way to add extra slots and functionality to the default inventory.
 * This does not override any vanilla behaviour such as inventory slots, it is purely an extension
 */
public abstract class AccessoryMenuExtension {
    private final AbstractContainerMenu menu;
    private final IAccessoryMenu accessoryMenu;
    private final Player owner;

    private List<AccessorySlot> slots = null;
    private boolean visible = false;

    public AccessoryMenuExtension(@NonNull AbstractContainerMenu menu, @NonNull Player owner) {
        this.menu = menu;
        this.accessoryMenu = (IAccessoryMenu) menu;
        this.owner = owner;
    }

    /**
     * Retrieve the actual {@link AbstractContainerMenu} instance which is also {@link #getAccessoryMenu()}
     * @return container menu bound to the extension interface holding this extension
     */
    public AbstractContainerMenu getMenu() {
        return menu;
    }

    /**
     * Retrieve the {@link IAccessoryMenu} which holds this as the active accessory menu extension
     * @return accessory menu interface holding this extension
     */
    public IAccessoryMenu getAccessoryMenu() {
        return accessoryMenu;
    }

    /**
     * Retrieve the owner of this accessory inventory
     * @return owner of the menu linked to the accessory extension
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Get a list of the accessory slots added to the extension's parent menu.
     * This is stored as to eliminate the need for looping through all the slots just to perform operations on our custom ones
     * @return a list of strictly {@link AccessorySlot}s added with the accessory extension
     */
    public List<AccessorySlot> getSlots() {
        return slots;
    }

    /**
     * Initially sets the list for added slots in the accessory extension.
     * This is called internally and should most likely not be replicated
     * @param slots added slots
     */
    public void setSlots(List<AccessorySlot> slots) {
        if (this.slots == null) {
            this.slots = slots;
        } else {
            throw new IllegalStateException("Slot list cannot be set as it has already been initialised");
        }
    }

    /**
     * Call {@link IAccessoryMenu#isAccessoryExtensionVisible()} instead
     * <p>
     * Determines whether the extension should be shown
     * @return {@code true} if the accessory extension should be shown, {@code false} otherwise
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Call {@link IAccessoryMenu#setAccessoryExtensionVisible(boolean)} instead
     * <p>
     * Set the visibility of the extension
     * @param value {@code true} to make the extension visible, {@code false} to hide it
     */
    public void setVisible(boolean value) {
        visible = value;

        if (owner.level().isClientSide()) {
            OhmegaNetworking.C2S.send(new SetExtensionVisiblePacket(value));
        }
    }

    /**
     * Add slots to the accessory inventory, these will be displayed in the extension pop-up.
     * You should ensure that you add the correct amount of slots ({@code AccessoryHelper.getSlotTypes().size()})
     * <p>
     * The server specific behaviour of adding accessory slots is handled internally by Ohmega
     * @param adder a function reference to {@link AbstractContainerMenu#addSlot(Slot)} with some behaviour automatically handled
     */
    public abstract void addSlotsClient(@NonNull SlotAdder adder);

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
    protected static @NonNull ItemStack tryMoveItemStackTo(@NonNull AbstractContainerMenu menu, @NonNull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
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
    public @NonNull ItemStack quickMoveStack(@NonNull AbstractContainerMenu menu, @NonNull Player player, int index, boolean considerExtensionSlots) {
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
                            //return ItemStack.EMPTY;
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
     * A simple interface to add slots to the extension
     */
    public interface SlotAdder {
        /**
         * Adds a slot to the accessory extension, with position relative to the extension itself
         * @param index slot index to add
         * @param x x-coordinate relative to the accessory extension
         * @param y y-coordinate relative to the accessory extension
         */
        void add(int index, int x, int y);
    }

    public interface Factory {
        @NonNull AccessoryMenuExtension construct(@NonNull AbstractContainerMenu menu, @NonNull Player player);
    }
}
