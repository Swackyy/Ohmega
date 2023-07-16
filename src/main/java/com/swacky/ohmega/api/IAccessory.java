package com.swacky.ohmega.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

// The base interface for all accessory items
public interface IAccessory {
    AccessoryType getType();

    // Gets called every tick, only if shouldTickUpdate returns true;
    default void onEquippedTick(LivingEntity entity, ItemStack stack) {
    }

    // Is called upon the player equipping the accessory
    default void onEquip(Player player, ItemStack stack) {
    }

    // Is called upon the player un-equipping the accessory
    default void onUnequip(Player player, ItemStack stack) {
    }

    // If true, the player can equip the accessory
    default boolean canEquip(LivingEntity entity) {
        return true;
    }

    // If true, the player can un-equip the accessory
    default boolean canUnequip() {
        return true;
    }

    // If true, will call onEquippedTick every tick when equipped
    default boolean shouldTickUpdate(LivingEntity entity) {
        return false;
    }
}
