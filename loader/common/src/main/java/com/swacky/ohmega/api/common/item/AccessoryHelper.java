package com.swacky.ohmega.api.common.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaTags;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A utility class containing many useful methods regarding accessories
 */
@SuppressWarnings("unused")
public final class AccessoryHelper {
    private static final @NonNull Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    /**
     * Retrieves the data on the {@link LivingEntity} pertaining to accessories
     * @param entity to retrieve data from
     * @return the accessory inventory data in the form of an {@link AccessoryData}
     */
    public static @NonNull AccessoryData getData(@NonNull LivingEntity entity) {
        return IMPL.getData(entity);
    }

    /**
     * You most likely want to use {@link #getType(Item)} instead
     * @param item the item to find the {@link AccessoryType}s of
     * @return all of the {@link AccessoryType}s that the {@link Item} is a part of, ignoring the priority index of each type
     */
    @SuppressWarnings("deprecation")
    public static @NonNull ImmutableSet<AccessoryType> getTypes(@NonNull Item item) {
        ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

        if (OhmegaConfig.Server.getData().disableAccessoryTypes().get()) {
            builder.add(AccessoryType.GENERIC.get());
        }

        for (Map.Entry<AccessoryType, TagKey<Item>> entry : OhmegaTags.getTags().entrySet()) {
            if (item.builtInRegistryHolder().is(entry.getValue())) {
                builder.add(entry.getKey());
            }
        }

        BooleanObjectPair<AccessoryType> override = AccessoryTypeManager.getTypeOverride(item);

        if (override != null) {
            builder.add(override.right());
        }

        ImmutableSet<AccessoryType> set = builder.build();

        if (set.isEmpty()) {
            return ImmutableSet.of(AccessoryType.NONE);
        }

        return set;
    }

    /**
     * Retrieves the accessory's effective {@link AccessoryType} (lowest priority index),
     * or the type returned by the AccessoryOverrideTypes(event/callback) if overridden
     * @param item the item to find the effective {@link AccessoryType} of
     * @return the {@link AccessoryType} of lowest priority index bound to the given accessory, or,
     * if no type can be found (including such a case where the item is not an accessory), then {@link AccessoryType#NONE}
     */
    @SuppressWarnings("deprecation")
    public static @NonNull AccessoryType getType(@NonNull Item item) {
        if (OhmegaConfig.Server.getData().disableAccessoryTypes().get()) {
            return AccessoryType.GENERIC.get();
        }

        AccessoryType type = AccessoryType.NONE;
        ImmutableList<AccessoryType> slotTypes = getSlotTypes();

        for (Map.Entry<AccessoryType, TagKey<Item>> entry : OhmegaTags.getTags().entrySet()) {
            if (item.builtInRegistryHolder().is(entry.getValue())) {
                AccessoryType candidate = entry.getKey();

                if (candidate.getPriority() < type.getPriority() && (type.isNoFallback() || slotTypes.contains(candidate)) || (!candidate.isNoFallback() && !slotTypes.contains(type))) {
                    type = candidate;
                }
            }
        }

        BooleanObjectPair<AccessoryType> override = AccessoryTypeManager.getTypeOverride(item);

        if (override != null && (override.leftBoolean() || type == AccessoryType.NONE)) {
            return override.right();
        }

        return type;
    }

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
            ItemAttributeModifiers modifiers = getSlotTypes().get(getSlot(stack)).getAttributeModifiers().getActive();

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
     * Retrieves the index types of each accessory index in entities' accessory inventories (determined by the server config)
     * @return a list of {@link AccessoryType}s matching indexes of accessory indexes
     */
    // todo: cache
    // todo: make a cached "getUniqueSlotTypes" that returns a Set
    public static @NonNull ImmutableList<AccessoryType> getSlotTypes() {
        List<? extends String> slotTypes = OhmegaConfig.Server.getData().slotTypes().getObject();

        if (slotTypes != null) {
            int size = slotTypes.size();
            ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(size);

            if (OhmegaConfig.Server.getData().disableAccessoryTypes().get()) {
                for (int i = 0; i < size; i++) {
                    builder.add(AccessoryType.GENERIC.get());
                }

                return builder.build();
            }

            for (String id : slotTypes) {
                AccessoryType type = AccessoryTypeManager.get(Identifier.parse(id));

                if (type != AccessoryType.NONE) {
                    builder.add(type);
                }
            }

            return builder.build();
        }

        return ImmutableList.of();
    }

    /**
     * Retrieves the types of accessory which can be key-bound (determined by the server config)
     * @return a set of {@link AccessoryType}s which can be key-bound
     */
    // todo: cache
    public static @NonNull ImmutableSet<AccessoryType> getKeyboundSlotTypes() {
        List<? extends String> types = OhmegaConfig.Server.getData().keyboundSlotTypes().getObject();

        if (types != null) {
            ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

            for (String id : types) {
                builder.add(AccessoryTypeManager.get(Identifier.parse(id)));
            }

            return builder.build();
        }

        return ImmutableSet.of();
    }

    /**
     * You should most likely use {@link #getBindTooltip(ItemStack)} as it is easier
     * and uses standardised key formats that work with {@link OhmegaLangHelper}
     * <p>
     * A utility method used to get a description for key-bound capable accessories
     * @param stack {@link ItemStack} instance of an accessory
     * @param bindKey the translatable key for use when a key-bind is applicable for this stack,
     * use '%s' for the bind key replacement in your translation
     * @param nonBindKey the translatable key for use when a key-bind is not applicable for this stack
     * @return example: "Press G to toggle flight", "Allows the wearer to fly"
     */
    public static @NonNull MutableComponent getBindTooltip(@NonNull ItemStack stack, @NonNull String bindKey, @NonNull String nonBindKey) {
        int slot = getSlot(stack);
        ImmutableList<AccessoryType> slotTypes = getSlotTypes();
        AccessoryType type;

        if (slot < 0 || slot >= slotTypes.size()) {
            type = null;
        } else {
            type = slotTypes.get(slot);
        }

        // Starts at -1 to align properly
        int typeIndex = 0;

        if (type != null) {
            for (int i = 0; i < slot; i++) {
                if (slotTypes.get(i) == type) {
                    typeIndex++;
                }
            }
        }

        Accessory accessory = Accessories.get(stack.getItem());
        boolean flag = false;

        if (accessory != null) {
            for (AccessoryType type0 : getKeyboundSlotTypes()) {
                if (slotTypes.contains(type0)) {
                    flag = true;
                    break;
                }
            }
        }

        KeyMapping mapping;

        if (type == null) {
            mapping = null;
        } else {
            mapping = OhmegaBinds.getMapping(type, typeIndex);
        }

        if (slot < 0 || !flag || mapping == null) {
            return Component.translatable(nonBindKey).withStyle(ChatFormatting.GRAY);
        }

        return Component.translatable(bindKey, mapping.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY);
    }

    /**
     * A shortcut method to {@link #getBindTooltip(ItemStack, String, String)} that uses standardised key formats
     * that work with {@link OhmegaLangHelper}
     * @param stack {@link ItemStack} instance of an accessory
     * @return example: "Press G to toggle flight", "Allows the wearer to fly"
     */
    public static @NonNull MutableComponent getBindTooltip(@NonNull ItemStack stack) {
        String id = stack.getItem().getDescriptionId();

        return getBindTooltip(stack, id + ".tooltip.keybind", id + ".tooltip");
    }

    /**
     * This is automatically applied internally
     * @param item accessory item
     * @return example: "Accessory Type: Utility"
     */
    public static @Nullable MutableComponent getTypeTooltip(@NonNull Item item) {
        AccessoryType type = getType(item);

        if (type.displayHoverText()) {
            return Component.translatable("accessory_type", type.getTranslation().getString()).withStyle(ChatFormatting.DARK_GRAY);
        }

        return null;
    }

    /**
     * Finds the first open index of
     * @param entity {@link LivingEntity} to search the accessory inventory of
     * @param type {@link AccessoryType} of index to find
     * @return index of the first open index matching the type, or {@code -1} if none is found
     */
    public static int getFirstOpenSlot(@NonNull LivingEntity entity, @NonNull AccessoryType type) {
        AccessoryData data = getData(entity);
        ImmutableList<AccessoryType> slotTypes = getSlotTypes();

        for (int i = 0; i < data.size(); i++) {
            if (slotTypes.get(i) == type && data.getStackInSlot(i).isEmpty()) {
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
            int slot = getFirstOpenSlot(entity, getType(item));

            if (slot >= 0) {
                ItemStack stack0 = stack.copyWithCount(1);

                if (getData(entity).setStack(entity, slot, stack0, EquipContext.USE_HELD)) {
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
        for (ItemStack other : getData(entity).getStacks()) {
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
    public static @NonNull NonNullList<ItemStack> getStacksFiltered(@NonNull LivingEntity entity, @NonNull Predicate<ItemStack> filter) {
        NonNullList<ItemStack> stacks = getData(entity).getStacks();
        NonNullList<ItemStack> filteredStacks = NonNullList.createWithCapacity(stacks.size());

        for (ItemStack stack : stacks) {
            if (filter.test(stack)) {
                filteredStacks.add(stack);
            }
        }

        return filteredStacks;
    }

    /**
     * Retrieve all of the {@link ItemStack}s in an entity's accessory inventory that are not empty.
     * @param entity {@link LivingEntity} to get accessory inventory data from
     * @return every non-empty {@link ItemStack} in the entity's accessory inventory
     */
    public static @NonNull NonNullList<ItemStack> getStacksNoEmpty(@NonNull LivingEntity entity) {
        return getStacksFiltered(entity, stack -> !stack.isEmpty());
    }

    /**
     * Check if an entity is wearing a certain {@link Item} in an accessory slot
     * @param entity {@link LivingEntity} to get accessory inventory data from
     * @param item the item to find
     * @return {@code true} if found, {@code false} otherwise
     */
    public static boolean hasAccessory(@NonNull LivingEntity entity, @NonNull Item item) {
        for (ItemStack stack : getData(entity).getStacks()) {
            if (stack.getItem() == item) {
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
        for (ItemStack stack : getData(entity).getStacks()) {
            if (stack.getItem() == item) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public interface Service {
        @NonNull AccessoryData getData(@NonNull LivingEntity entity);
    }
}
