package com.swacky.ohmega.api.common.item;

import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Holds data related to accessory items
 */
public final class Accessories {
    private static final @NonNull Map<Item, Accessory> BOUND_ACCESSORIES = new IdentityHashMap<>();

    /**
     * @param item the item to get the binding of
     * @return the {@link Accessory} binding
     */
    public static @Nullable Accessory get(@NonNull Item item) {
        Accessory candidate = BOUND_ACCESSORIES.get(item);

        if (candidate != null) {
            return candidate;
        }

        if (item instanceof IAccessory binding) {
            Accessory accessory = new Accessory(binding);

            BOUND_ACCESSORIES.put(item, accessory);
            return accessory;
        }

        return null;
    }

    /**
     * Check if an item is registered as an accessory, either by implementing {@link Accessory} in your {@link Item} class
     * or by calling {@link #bind(Item, IAccessory)},
     * @param item the item to check if it is bound
     * @return {@code true} if the {@link Item} class implements {@link Accessory} or is accessory bound by code ({@link #bind(Item, IAccessory)}
     */
    public static boolean isBound(@NonNull Item item) {
        return get(item) != null;
    }

    /**
     * Use this to bind an {@link Accessory} instance to an {@link Item}, essentially turning it into an accessory.
     * If you have read access on the type of item (if the item class is yours), instead implement the {@link Accessory} interface
     * @param item item to bind
     * @param binding the {@link Accessory} instance to store on the item, determining most accessory behaviour
     * @return {@code true} if successfully bound, {@code false} otherwise for any given reason (subject to change)
     */
    public static boolean bind(@NonNull Item item, @NonNull IAccessory binding) {
        if (item instanceof AirItem) {
            return false;
        }

        BOUND_ACCESSORIES.put(item, new Accessory(binding));
        return true;
    }
}
