package com.swacky.ohmega.api;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The base interface for all accessory items.
 * To make an item an accessory, either:
 * <ul>
 *     <li>Make your {@link net.minecraft.world.item.Item} class inherit this interface (recommended for your own items)</li>
 *     <li>Bind an {@link IAccessory} instance with {@link AccessoryHelper#bindAccessory(Item, IAccessory)} (for vanilla and other mods' items)</li>
 * </ul>
 * <p>
 * <a href="https://github.com/Swackyy/Ohmega/wiki">Refer to the wiki</a> or {@link com.swacky.ohmega.common.item.AngelRing} for examples
 */
@SuppressWarnings("unused")
public interface IAccessory {
    // Is called every tick when equipped
    default void tick(@NonNull Player player, @NonNull ItemStack stack) {}

    // Is called upon the player equipping the accessory
    default void onEquip(@NonNull Player player, @NonNull ItemStack stack) {}

    // Is called upon the player un-equipping the accessory
    default void onUnequip(@NonNull Player player, @NonNull ItemStack stack) {}

    // If true, the player can equip the accessory
    default boolean canEquip(@NonNull Player player, @NonNull ItemStack stack) {
        return true;
    }

    // If true, the player can un-equip the accessory
    default boolean canUnequip(@NonNull Player player, @NonNull ItemStack stack) {
        return true;
    }

    // Called when the user chooses, not internally
    // Simply a common method that is not intended to behave in a specific way
    @Deprecated(since = "1.5.16")
    default void update(@NonNull Player player, @NonNull ItemStack stack) {}

    // Override this to make accessories only equipable when certain other accessories are not equipped
    // Default behaviour prevents players from equipping two of the same accessory at once
    /**
     * This method takes priority over {@link #compatibleWith(IAccessory)} when overridden, which is deprecated
     */
    default boolean compatibleWith(@NonNull ItemStack other) {
        return AccessoryHelper.getBoundAccessory(other.getItem()) != this;
    }

    // Override this to make accessories only equipable when certain other accessories are not equipped
    // Default behaviour prevents players from equipping two of the same accessory at once
    /**
     * Still works, but {@link #compatibleWith(ItemStack)} will take priority if overridden
     * <p>
     * Will be removed by {@code v1.6.0}
     */
    @Deprecated(since = "1.5.4")
    default boolean compatibleWith(@NonNull IAccessory other) {
        return this != other;
    }

    // Called when a key-bind is pressed for this slot. Will only work for accessories of key-bound types.
    // It is recommended that when this is overridden and used, that a tooltip will be provided,
    // a component for the tooltip can be acquired from the AccessoryHelper utility class.
    default void onUse(@NonNull Player player, @NonNull ItemStack stack) {}

    // If true, will automatically synchronise with the client every tick when worn
    default boolean autoSync(@NonNull Player player, @NonNull ItemStack stack) {
        return false;
    }

    // Attribute modifiers to be applied when the accessory is equipped (or only when active)
    default void addDefaultAttributeModifiers(AccessoryModifiers.@NonNull Builder builder) {}

     // The sound to be played if the accessory is equipped through right-clicking the held item
     // A replacement for the vanilla method to ensure easier compatibility
    @Nullable
    default Holder<SoundEvent> getEquipSound() {
        return null;
    }
}
