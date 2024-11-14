package com.swacky.ohmega.api;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The base interface for all accessory items
 * <p>
 * Extend from this to make your item an accessory
 */
@SuppressWarnings("unused")
public interface IAccessory {
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
    default boolean canEquip(Player player, ItemStack stack) {
        return true;
    }

    // If true, the player can un-equip the accessory
    default boolean canUnequip(Player player, ItemStack stack) {
        return true;
    }

    // Called when the user chooses, not internally
    // Simply a common method that is not intended to behave in a specific way
    default void update(Player player, ItemStack stack) {
    }

    // Override this to make accessories only equipable when certain other accessories are not equipped. Default is just self
    default boolean checkCompatibility(IAccessory other) {
        return this != other;
    }

    // Called when a key bind is pressed for this slot. Will only work for utility and special slots.
    // It is recommended that when this is overridden and used, that a tooltip will be provided.
    // A component for the tooltip can be acquired from the AccessoryHelper utility class.
    default void onUse(Player player, ItemStack stack) {
    }

    // If true, will automatically sync with the client every tick
    default boolean autoSync(ServerPlayer player) {
        return false;
    }

    // Attribute modifiers to be applied when the accessory is equipped (or only when active)
    default void addDefaultAttributeModifiers(ModifierHolder.Builder builder) {
    }

    // A replacement for the vanilla for Minecraft versions 1.19.4+
    @Nullable
    default Holder<SoundEvent> getEquipSound() {
        return null;
    }
}
