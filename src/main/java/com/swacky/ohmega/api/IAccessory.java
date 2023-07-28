package com.swacky.ohmega.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

// The base interface for all accessory items
public interface IAccessory {
    @NotNull AccessoryType getType();

    // Gets called every tick
    default void tick(Player player, ItemStack stack) {
    }

    // Is called upon the player equipping the accessory
    default void onEquip(Player player, ItemStack stack) {
    }

    // Is called upon the player un-equipping the accessory
    default void onUnequip(Player player, ItemStack stack) {
    }

    // If true, the player can equip the accessory
    default boolean canEquip(Player player) {
        return true;
    }

    // If true, the player can un-equip the accessory
    default boolean canUnequip(Player player) {
        return true;
    }

    // Called when the user chooses, such as for the fly ring (example item), this could be utilised to make sure the player can still fly upon switching game modes.
    default void update(Player player) {
    }

    // Called when a key bind is pressed for this slot. Will only work for utility and special slots.
    // It is recommended that when this is overridden and used, that a tooltip will be provided,
    // A component for the tooltip can be acquired from the AccessoryHelper utility class.
    default void onUse(Player player, ItemStack stack) {
    }

    // If true, will automatically sync with the client every tick
    default boolean autoSync(Player player) {
        return false;
    }
}
