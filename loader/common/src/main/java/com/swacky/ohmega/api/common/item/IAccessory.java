package com.swacky.ohmega.api.common.item;

import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The base interface for all accessory items.
 * <a href="https://github.com/Swackyy/Ohmega/wiki">Refer to the wiki</a> or {@link AngelRing} for examples
 * <p>
 * To make an item an accessory, either:
 * <ul>
 *     <li>Make your {@link Item} class inherit this interface (recommended for your own items)</li>
 *     <li>Bind an {@link IAccessory} instance with {@link Accessories#bind(Item, IAccessory)} (intended for vanilla and other mods' items)</li>
 * </ul>
 * <p>
 * One technical feature to be aware of is that internally, accessory instances are not stored with this class.
 * They are stored as the decorated {@link Accessory} class, so try not to store instances of {@link IAccessory},
 * but the wrapper instead to help with performance
 */
public interface IAccessory {
    /**
     * Called every tick when equipped in an accessory slot if {@link #preferInventoryTick(ItemStack)} returns {@code false}
     * Specifically, it is called at the end of {@link LivingEntity#tick()}
     * @param entity the {@link LivingEntity} wearing this accessory
     * @param stack the {@link ItemStack} of this accessory item being worn
     */
    default void accessoryTick(@NonNull LivingEntity entity, @NonNull ItemStack stack) {}

    /**
     * Check whether {@link Item#inventoryTick(ItemStack, ServerLevel, Entity, EquipmentSlot)} should be called
     * <strong>instead of</strong> {@link #accessoryTick(LivingEntity, ItemStack)} when equipped in an accessory slot
     * <p>
     * Take note that {@link Item#inventoryTick(ItemStack, ServerLevel, Entity, EquipmentSlot)} is only called on the server side
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @return
     * <ul>
     *     <li>{@code true}: only call {@link Item#inventoryTick(ItemStack, ServerLevel, Entity, EquipmentSlot)} every tick when equipped</li>
     *     <li>{@code false}: only call {@link #accessoryTick(LivingEntity, ItemStack)} every tick when equipped</li>
     * </ul>
     */
    default boolean preferInventoryTick(@NonNull ItemStack stack) {
        return false;
    }

    /**
     * Called upon the entity equipping the accessory
     * @param entity the {@link LivingEntity} equipping this accessory
     * @param stack the {@link ItemStack} of this accessory item being equipped
     * @param context context surrounding how the accessory was equipped
     */
    default void onEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {}

    /**
     * Called upon the entity un-equipping the accessory
     * @param entity the {@link LivingEntity} un-equipping this accessory
     * @param stack the {@link ItemStack} of this accessory item being un-equipped
     */
    default void onUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {}

    /**
     * Dictates if the entity can wear the accessory
     * @param entity the {@link LivingEntity} trying to equip this accessory
     * @param stack the {@link ItemStack} of this accessory item being (possibly) equipped
     * @param context context surrounding how the accessory may be equipped
     * @return {@code true} if it should be allowed to be worn, {@code false} otherwise
     */
    default boolean canEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        return true;
    }

    /**
     * Dictates if the entity can take off the accessory
     * @param entity the {@link LivingEntity} trying to un-equip this accessory
     * @param stack the {@link ItemStack} of this accessory item being (possibly) un-equipped
     * @return {@code true} if it should be allowed to be unworn, {@code false} otherwise
     */
    default boolean canUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        return true;
    }

    /**
     * Determines if accessories can be worn when others are.
     * Called against every other currently accessory in the accessory inventory.
     * By default, Ohmega prevents entities from equipping two of the same accessory at once
     * @param stack the {@link ItemStack} of this accessory to test with
     * @param other the {@link ItemStack} of another accessory to test against
     * @return {@code true} if compatible, {@code false} otherwise
     */
    default boolean compatibleWith(@NonNull ItemStack stack, @NonNull ItemStack other) {
        Accessory accessory = Accessories.get(other.getItem());

        if (accessory != null) {
            return accessory.unwrap() != this;
        }

        return true;
    }

    /**
     * Called when this accessory is worn and its corresponding slot's key-bind is pressed.
     * This will only work for accessories of key-bound types, configurable in the server config.
     * <p>
     * It is recommended that when this is overridden and used, that a tooltip will be provided,
     * a component for the tooltip can be acquired from {@link AccessoryHelper#getBindTooltip(ItemStack)}.
     * @param player the {@link Player} wearing this accessory
     * @param stack {@link ItemStack} instance of the accessory in the slot which key-bind has been pressed
     * @return {@code true} if a packet should be sent to the server requesting this method to be invoked, {@code false} otherwise.
     * Useful for conserving network usage if it is guaranteed nothing will occur on either side.
     * Implementing custom behaviour for this method will likely mean you will have to return {@code true}
     */
    // todo: split into 'onKeybindPress', 'onKeybindHold' and 'onKeybindRelease' or similar
    default boolean onKeybindUse(@NonNull Player player, @NonNull ItemStack stack) {
        return false;
    }

    /**
     * Dictates whether Ohmega should synchronise the server's {@link ItemStack} instance with each client every tick.
     * Could be useful for stack data component changes, which are not automatically discovered.
     * <p>
     * If possible, let Ohmega do the syncing internally instead of using this which adds additional network overhead
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @return {@code true} if the server should send updates to clients every tick, {@code false} otherwise
     */
    default boolean autoSync(@NonNull ItemStack stack) {
        return false;
    }

    /**
     * Allows you to choose on which 'n' ticks that the server state of accessories of this type will be updated to clients,
     * <strong>this never includes the zeroth tick, and returning {@code 0} will never sync at all</strong>
     * <p>
     * This will only have an effect if {@link #autoSync(ItemStack)} returns {@code true}.
     * Higher values may have more noticeable lag on clients, but will incur less network overhead
     * <p>
     * This has a {@code byte} return type as to discourage using particularly large numbers, as they shouldn't have any use in the first place.
     * A consequence of this choice is that slightly less memory is used per tick, of course, which is always good
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @return the modulo operand to use checking for which 'n' ticks the {@link ItemStack} should be updated
     */
    default byte autoSyncModulo(@NonNull ItemStack stack) {
        return 5;
    }

    /**
     * Determines if the vanilla {@link Item#use(Level, Player, InteractionHand)} will be preferred over Ohmega's built-in
     * right-click to equip behaviour ({@link AccessoryHelper#tryEquip(LivingEntity, ItemStack)})
     * @param stack the {@link ItemStack} of this accessory item which is right-clicked
     * @return
     * <ul>
     *     <li>
     *         {@code true}: {@link Item#use(Level, Player, InteractionHand)} will run before,
     *         and if it returns a consuming {@link InteractionResult}, then the accessory will not be right-click-equipped
     *     </li>
     *     <li>
     *         {@code false}: Ohmega's right-click to equip behaviour will run first, and will only call {@link Item#use(Level, Player, InteractionHand)}
     *         if {@link AccessoryHelper#tryEquip(LivingEntity, ItemStack)} returns a non-consuming {@link InteractionResult}
     *     </li>
     * </ul>
     */
    default boolean preferVanillaUse(@NonNull ItemStack stack) {
        return true;
    }

    /**
     * The sound to be played if the accessory is equipped through right-clicking this accessory binding as the held item
     * <p>
     * This is a replacement for the vanilla method to ensure easier compatibility and to add volume and pitch control
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @return sound to be played
     */
    @Nullable
    default SoundData getEquipSound(@NonNull ItemStack stack) {
        return null;
    }

    /**
     * Allows for making accessories which act like leather boots, preventing sinking in powdered snow
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @return {@code true} if walking on powdered snow should be allowed, {@code false} otherwise
     */
    default boolean allowWalkOnPowderSnow(@NonNull ItemStack stack) {
        return false;
    }

    /**
     * Will allow for changing mob visibility of entities wearing this accessory.
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @param targetingEntity the entity being targeted.
     * @return the multiplier to submit, a value of {@code 1} will mean no change, and {@code 0} will prevent visibility at all
     */
    default double getMobVisibilityMultiplier(@NonNull ItemStack stack, @Nullable Entity targetingEntity) {
        return 1;
    }

    /**
     * Allows an accessory to act as a gold armour piece, preventing piglins from targeting the wearer
     * @param stack the {@link ItemStack} of this accessory item being worn
     * @return {@code true} to prevent piglin targeting, {@code false} otherwise
     */
    default boolean isPiglinSafe(@NonNull ItemStack stack) {
        return false;
    }
}
