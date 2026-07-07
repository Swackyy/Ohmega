package com.swacky.ohmega.api.common.item;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A utility class containing many useful methods regarding accessories
 */
@SuppressWarnings("unused")
public final class AccessoryHelper {
    /**
     * Checks the active state of an accessory item
     * @param stack {@link ItemStack} to seek active state from
     * @return {@code true} if active, {@code false} if inactive
     */
    public static boolean isActive(@NonNull ItemStack stack) {
        Boolean active = stack.get(OhmegaDataComponents.getActive());

        if (active != null) {
            return active;
        }

        return false;
    }

    /**
     * Sets the active state of an accessory item.
     * @param entity the entity wearing (or going to equip) the accessory
     * @param stack {@link ItemStack} to set active state of
     * @param value {@code true} if active, {@code false} if inactive
     */
    public static void setActive(@NonNull LivingEntity entity, @NonNull ItemStack stack, boolean value) {
        stack.set(OhmegaDataComponents.getActive(), value);

        if (value) {
            ItemAttributeModifiers modifiers = OhmegaDataAttachments.getData(entity).getEntry(getSlot(stack)).getType().getAttributeModifiers().getActive();

            stack.set(OhmegaDataComponents.getSlotActiveModifiers(), modifiers);
            changeModifiers(entity, modifiers, true);
        } else {
            // This separate approach is needed, prevents a strange crash
            changeModifiers(entity, stack.get(OhmegaDataComponents.getSlotActiveModifiers()), false);
        }

        changeModifiers(entity, stack.get(OhmegaDataComponents.getAccessoryActiveModifiers()), value);
    }

    /**
     * Toggles the active state of an accessory
     * @param entity the entity wearing the accessory
     * @param stack {@link ItemStack} to set active state of
     */
    public static void toggleActive(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        setActive(entity, stack, !isActive(stack));
    }

    /**
     * Retrieves the known index of an accessory
     * @param stack {@link ItemStack} to seek data from
     * @return the index when equipped, or {@code -1} when not equipped
     */
    public static int getSlot(@NonNull ItemStack stack) {
        Integer slot = stack.get(OhmegaDataComponents.getSlotIndex());

        if (slot != null) {
            return slot;
        }

        return -1;
    }

    /**
     * This is handled internally, you most likely won't have to use this
     * @param stack {@link ItemStack} to set index of
     * @param index the index to set to
     */
    public static void setSlot(@NonNull ItemStack stack, int index) {
        stack.set(OhmegaDataComponents.getSlotIndex(), index);
    }

    /**
     * This is handled internally, you most likely won't have to use this
     * <p>
     * Sets the index of the index to {@code -1}
     * @param stack {@link ItemStack} to set index of
     */
    public static void setNoSlot(@NonNull ItemStack stack) {
        stack.remove(OhmegaDataComponents.getSlotIndex());
    }

    /**
     * Utility function to add attribute modifiers to a {@link LivingEntity}
     * @param entity {@link LivingEntity} to add/remove modifiers to/from
     * @param modifiers to add or remove
     * @param add if {@code true}, will add the attribute modifiers to the {@link LivingEntity},
     * if {@code false} existing ones will be removed
     */
    public static void changeModifiers(@NonNull LivingEntity entity, @Nullable ItemAttributeModifiers modifiers, boolean add) {
        if (modifiers != null) {
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                AttributeInstance attribute = entity.getAttribute(entry.attribute());

                if (attribute != null) {
                    if (add) {
                        if (!attribute.hasModifier(entry.modifier().id())) {
                            attribute.addTransientModifier(entry.modifier());
                        }
                    } else {
                        attribute.removeModifier(entry.modifier());
                    }
                }
            }
        }
    }

    /**
     * Finds the first open index of
     * @param entity {@link LivingEntity} to search the accessory inventory of
     * @param type {@link AccessoryType} of index to find
     * @return index of the first open index matching the type, or {@code -1} if none is found
     */
    public static int getFirstOpenSlot(@NonNull LivingEntity entity, @NonNull AccessoryType type) {
        AccessoryData data = OhmegaDataAttachments.getData(entity);
        int size = data.size();

        for (int i = 0; i < size; i++) {
            AccessoryDataEntry entry = data.getEntry(i);

            if (entry.getType().equals(type) && entry.getStack().isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * This is automatically handled internally
     * <p>
     * Used when trying to equip an accessory via right-clicking the held item
     * @param entity {@link LivingEntity} to equip the accessory on
     * @param stack the right-clicked held {@link ItemStack}
     * @return {@link InteractionResult#SUCCESS} if equipped successfully, else {@link InteractionResult#PASS}
     */
    public static @NonNull InteractionResult tryEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null) {
            int index = getFirstOpenSlot(entity, Accessories.getType(entity, item));

            if (index >= 0) {
                ItemStack stack0 = stack.copyWithCount(1);

                if (OhmegaDataAttachments.getData(entity).getEntry(index).setStack(entity, stack0, index, EquipContext.USE_HELD)) {
                    stack.consume(1, entity);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Checks if two accessories are compatible with each other by testing both ways
     * @param first one accessory {@link ItemStack}
     * @param second a second accessory {@link ItemStack}
     * @return {@code true} if both are compatible with each other, {@code false} otherwise
     */
    public static boolean compatibleWith(@NonNull ItemStack first, @NonNull ItemStack second) {
        Accessory firstAccessory = Accessories.get(first.getItem());

        if (firstAccessory != null) {
            Accessory secondAccessory = Accessories.get(second.getItem());

            if (secondAccessory != null) {
                return firstAccessory.compatibleWith(first, second) && secondAccessory.compatibleWith(second, first);
            }
        }

        return false;
    }

    /**
     * Checks if an accessory is able to be worn, testing the target accessory {@link ItemStack} against every other worn accessory
     * @param entity {@link LivingEntity} to get accessory inventory data from
     * @param stack accessory {@link ItemStack} to test against every other accessory currently worn by the entity
     * @return {@code true} if the target accessory is compatible with every other worn accessory, {@code false} otherwise
     */

    public static boolean compatibleWith(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(entity).getEntries()) {
            ItemStack other = entry.getStack();

            if (!other.isEmpty() && !compatibleWith(stack, other))  {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieve all of the {@link ItemStack}s in an entity's accessory inventory that match a given filter
     * @param entity {@link LivingEntity} to get accessory inventory data from
     * @param filter A predicate filter to allow or deny elements from the returned list
     * @return every matching {@link ItemStack} in the entity's accessory inventory
     */
    public static @NonNull List<ItemStack> getStacksFiltered(@NonNull LivingEntity entity, @NonNull Predicate<ItemStack> filter) {
        AccessoryData data = OhmegaDataAttachments.getData(entity);
        List<ItemStack> filteredStacks = new ArrayList<>(data.size());

        for (AccessoryDataEntry entry : data.getEntries()) {
            ItemStack stack = entry.getStack();

            if (filter.test(stack)) {
                filteredStacks.add(stack);
            }
        }

        return filteredStacks;
    }

    /**
     * Check if an entity is wearing a certain {@link Item} in an accessory slot
     * @param entity {@link LivingEntity} to get accessory inventory data from
     * @param item the item to find
     * @return {@code true} if found, {@code false} otherwise
     */
    public static boolean hasAccessory(@NonNull LivingEntity entity, @NonNull Item item) {
        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(entity).getEntries()) {
            if (entry.getStack().getItem() == item) {
                return true;
            }
        }

        return false;
    }

    /**
     * Find the first {@link ItemStack} of a certain item
     * @param entity {@link LivingEntity} to get accessory inventory data from
     * @param item the item to find
     * @return the found matching {@link ItemStack}, or else {@link ItemStack#EMPTY}
     */
    public static @NonNull ItemStack getStack(@NonNull LivingEntity entity, @NonNull Item item) {
        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(entity).getEntries()) {
            ItemStack stack = entry.getStack();

            if (stack.getItem() == item) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
