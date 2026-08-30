package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * A small, bare-bones interface version of an accessory slot,
 * provides a structure to access the {@link AccessoryType} of the slot and its held {@link ItemStack}
 */
public interface IAccessorySlot {
    @NonNull AccessoryType getType();

    @NonNull ItemStack getItem();

    default boolean hasItem() {
        return !getItem().isEmpty();
    }
}
