package com.swacky.ohmega.api;

import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
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
    /**
     * Called every tick when equipped in an accessory slot
     * @param player the {@link Player} wearing this accessory
     * @param stack the {@link ItemStack} of this accessory item being worn
     */
    default void accessoryTick(@NonNull Player player, @NonNull ItemStack stack) {}

    /**
     * Called upon the player equipping the accessory
     * @param player the {@link Player} equipping this accessory
     * @param stack the {@link ItemStack} of this accessory item being equipped
     */
    default void onEquip(@NonNull Player player, @NonNull ItemStack stack) {}

    /**
     * Called upon the player un-equipping the accessory
     * @param player the {@link Player} un-equipping this accessory
     * @param stack the {@link ItemStack} of this accessory item being un-equipped
     */
    default void onUnequip(@NonNull Player player, @NonNull ItemStack stack) {}

    /**
     * Dictates if the player can wear the accessory
     * @param player the {@link Player} trying to equip this accessory
     * @param stack the {@link ItemStack} of this accessory item being (possibly) equipped
     * @return {@code true} if it should be allowed to be worn, {@code false} otherwise
     */
    default boolean canEquip(@NonNull Player player, @NonNull ItemStack stack) {
        return true;
    }

    /**
     * Dictates if the player can take off the accessory
     * @param player the {@link Player} trying to un-equip this accessory
     * @param stack the {@link ItemStack} of this accessory item being (possibly) un-equipped
     * @return {@code true} if it should be allowed to be unworn, {@code false} otherwise
     */
    default boolean canUnequip(@NonNull Player player, @NonNull ItemStack stack) {
        return true;
    }

    /**
     * Determines if accessories can be worn when others are.
     * Called against every other currently accessory in the accessory inventory.
     * By default, Ohmega prevents players from equipping two of the same accessory at once
     * @param other the {@link ItemStack} of another accessory to test against
     * @return {@code true} if compatible, {@code false} otherwise
     */
    default boolean compatibleWith(@NonNull ItemStack other) {
        return AccessoryHelper.getAccessory(other.getItem()) != this;
    }

    // Called when a key-bind is pressed for this index. Will only work for accessories of key-bound types.
    // It is recommended that when this is overridden and used, that a tooltip will be provided,
    // a component for the tooltip can be acquired from the AccessoryHelper utility class.

    /**
     * Called when this accessory is worn and its corresponding slot's key-bind is pressed.
     * This will only work for accessories of key-bound types, configurable in the server config.
     * <p>
     * It is recommended that when this is overridden and used, that a tooltip will be provided,
     * a component for the tooltip can be acquired from {@link AccessoryHelper#getBindTooltip(ItemStack)}.
     * @param player the {@link Player} wearing this accessory
     * @param stack {@link ItemStack} instance of the accessory in the slot which key-bind has been pressed
     */
    default void onKeybindUse(@NonNull Player player, @NonNull ItemStack stack) {}

    /**
     * Dictates whether Ohmega should synchronise the server's {@link ItemStack} instance with each client every tick.
     * Could be useful for stack data component changes, which are not automatically discovered.
     * <p>
     * If possible, use {@link AccessoryContainer#setChanged(int)} to indicate that a change has been made,
     * instead of using this which adds additional network overhead
     * @param player the {@link Player} wearing this accessory
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @return {@code true} if the server should send updates to clients every tick, {@code false} otherwise
     */
    default boolean autoSync(@NonNull Player player, @NonNull ItemStack stack) {
        return false;
    }

    /**
     * Add attribute modifiers to be applied under certain conditions using the provided {@link AccessoryModifiers.Builder}
     * @param builder the builder provided
     */
    default void addAttributeModifiers(AccessoryModifiers.@NonNull Builder builder) {}

    /**
     * The sound to be played if the accessory is equipped through right-clicking this accessory binding as the held item
     * <p>
     * This is a replacement for the vanilla method to ensure easier compatibility
     * @return sound to be played
     */
    @Nullable
    default Holder<SoundEvent> getEquipSound() {
        return null;
    }
}
