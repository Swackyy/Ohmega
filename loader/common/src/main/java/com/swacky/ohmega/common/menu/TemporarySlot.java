package com.swacky.ohmega.common.menu;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * Temporary minimal overhead dummy slot to fix an annoying edge case.
 * These slots are not retained as the extension slots for long and are quickly swapped out when they are no longer needed
 */
// todo: this may not be needed after some refactoring, but works for now
public class TemporarySlot extends Slot {
    private static final SimpleContainer EMPTY_CONTAINER = new SimpleContainer(0);

    public TemporarySlot() {
        super(EMPTY_CONTAINER, 0, 0, 0);
    }

    @Override
    public @NonNull ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void set(@NonNull ItemStack stack) {}

    @Override
    public void setChanged() {}

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public @NonNull ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }
}
