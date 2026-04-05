package com.swacky.ohmega.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaTags;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public final class AccessoryHelper {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    /**
     * Retrieves the data on the {@link Player} pertaining to worn accessories
     * @param player user to retrieve data from
     * @return the accessory inventory data in the form of an {@link AccessoryData}
     */
    public static AccessoryData getData(Player player) {
        return IMPL.getData(player);
    }

    /**
     * Use this to bind an {@link IAccessory} instance to an {@link Item}, essentially turning it into an accessory.
     * If you have read access on the type of item (if the item class is yours), instead implement the {@link IAccessory} interface
     * @param item item to bind
     * @param binding the {@link IAccessory} instance to store on the item, determining most accessory behaviour
     * @return {@code true} if it worked, false if the item is already bound or is air
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        return Ohmega.bindAccessory(item, binding);
    }

    /**
     * Check if an item is registered as an accessory, either by implementing {@link IAccessory} in your {@link Item} class
     * or by calling {@link #bindAccessory(Item, IAccessory)},
     * @param item the item to check if it is bound
     * @return {@code true} if the {@link Item} class implements {@link IAccessory} or is accessory bound by code ({@link #bindAccessory(Item, IAccessory)}
     */
    public static boolean isAccessory(Item item) {
        return Ohmega.isAccessory(item);
    }

    /**
     * @param item the item to get the binding of
     * @return the {@link IAccessory} binding
     */
    public static IAccessory getAccessory(Item item) {
        return Ohmega.getAccessory(item);
    }

    /**
     * You most likely want to use {@link #getType(Item)} instead
     * @param item the item to find the {@link AccessoryType}s of
     * @return all of the {@link AccessoryType}s that the {@link Item} is a part of, ignoring the priority index of each type
     */
    @SuppressWarnings("deprecation")
    public static ImmutableList<AccessoryType> getTypes(Item item) {
        ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

        if (OhmegaConfig.Server.disableAccessoryTypes()) {
            builder.add(AccessoryType.GENERIC.get());
        }

        AccessoryType override = AccessoryTypeManager.getTypeOverride(item);

        if (override != null) {
            builder.add(override);
        }

        for (Map.Entry<AccessoryType, TagKey<Item>> entry : OhmegaTags.getTags().entrySet()) {
            if (item.builtInRegistryHolder().is(entry.getValue())) {
                builder.add(entry.getKey());
            }
        }

        ImmutableSet<AccessoryType> set = builder.build();

        if (set.isEmpty()) {
            return ImmutableList.of(AccessoryType.NORMAL.get());
        }

        return ImmutableList.copyOf(set);
    }

    /**
     * Retrieves the accessory's effective {@link AccessoryType} (lowest priority index),
     * or the type returned by the AccessoryOverrideTypes(event/callback) if overridden
     * @param item the item to find the effective {@link AccessoryType} of
     * @return the {@link AccessoryType} of lowest priority index bound to the given accessory, or,
     * if no type can be found (including such a case where the item is not an accessory), then {@link AccessoryType#NONE}
     */
    @SuppressWarnings("deprecation")
    public static AccessoryType getType(Item item) {
        if (OhmegaConfig.Server.disableAccessoryTypes()) {
            return AccessoryType.GENERIC.get();
        }

        AccessoryType override = AccessoryTypeManager.getTypeOverride(item);

        if (override != null) {
            return override;
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

        return type;
    }

    /**
     * Checks the active state of an accessory item
     * @param stack {@link ItemStack} to seek active state from
     * @return {@code true} if active, {@code false} if inactive
     */
    public static boolean isActive(ItemStack stack) {
        Boolean active = stack.get(OhmegaDataComponents.getActive());

        if (active != null) {
            return active;
        }

        return false;
    }

    /**
     * Sets the active state of an accessory item.
     * @param player the player wearing (or going to equip) the accessory
     * @param stack {@link ItemStack} to set active state of
     * @param value {@code true} if active, {@code false} if inactive
     */
    public static void setActive(Player player, ItemStack stack, boolean value) {
        stack.set(OhmegaDataComponents.getActive(), value);

        if (value) {
            ItemAttributeModifiers activeModifiers = getSlotTypes().get(getSlot(stack)).getAttributeModifiers().getActive();

            stack.set(OhmegaDataComponents.getSlotActiveModifiers(), activeModifiers);
            changeModifiers(player, activeModifiers, true);
        } else {
            ItemAttributeModifiers activeModifiers = stack.get(OhmegaDataComponents.getSlotActiveModifiers());

            if (activeModifiers != null) {
                changeModifiers(player, activeModifiers, false);
            }
        }

        changeModifiers(player, getModifiers(stack).getActive(), value);
    }

    /**
     * Toggles the active state of an accessory
     * @param player the player wearing the accessory
     * @param stack {@link ItemStack} to set active state of
     */
    public static void toggleActive(Player player, ItemStack stack) {
        setActive(player, stack, !isActive(stack));
    }

    /**
     * Retrieves the known index of an accessory
     * @param stack {@link ItemStack} to seek data from
     * @return the index when equipped, or {@code -1} when not equipped
     */
    public static int getSlot(ItemStack stack) {
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
    public static void setSlot(ItemStack stack, int index) {
        stack.set(OhmegaDataComponents.getSlotIndex(), index);
    }

    /**
     * This is handled internally, you most likely won't have to use this
     * <p>
     * Sets the index of the index to {@code -1}
     * @param stack {@link ItemStack} to set index of
     */
    public static void setNoSlot(ItemStack stack) {
        setSlot(stack, -1);
    }

    /**
     * Retrieves the {@link AccessoryModifiers} from an accessory
     * @param stack {@link ItemStack} of an accessory to find the modifiers of
     * @return the {@link AccessoryModifiers} to apply when accessory equipped
     */
    public static AccessoryModifiers getModifiers(ItemStack stack) {
        IAccessory accessory = getAccessory(stack.getItem());

        if (accessory != null) {
            AccessoryModifiers.Builder builder = new AccessoryModifiers.Builder();

            // todo: cache this
            accessory.addAttributeModifiers(builder);
            OhmegaHooks.accessoryAttributeModifiersEvent(stack, builder);
            return builder.build();
        }

        return AccessoryModifiers.EMPTY;
    }

    /**
     * Utility function to add attribute modifiers to a {@link Player} from {@link AccessoryModifiers}
     * @param player {@link Player} to add/remove modifiers to/from
     * @param modifiers to add or remove
     * @param add if {@code true}, will add the attribute modifiers to the {@link Player},
     * if {@code false} existing ones will be removed
     */
    public static void changeModifiers(Player player, ItemAttributeModifiers modifiers, boolean add) {
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            AttributeInstance attribute = player.getAttribute(entry.attribute());

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

    /**
     * Retrieves the index types of each accessory index in players' accessory inventories (determined by the server config)
     * @return a list of {@link AccessoryType}s matching indexes of accessory indexes
     */
    // todo: cache
    // todo: make a cached "getUniqueSlotTypes" that returns a Set
    public static ImmutableList<AccessoryType> getSlotTypes() {
        List<String> slotTypes = OhmegaConfig.Server.slotTypes();
        int size = slotTypes.size();
        ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(size);

        if (OhmegaConfig.Server.disableAccessoryTypes()) {
            for (int i = 0; i < size; i++) {
                builder.add(AccessoryType.GENERIC.get());
            }

            return builder.build();
        }

        for (String id : slotTypes) {
            builder.add(AccessoryTypeManager.get(Identifier.parse(id)));
        }

        return builder.build();
    }

    /**
     * Retrieves the types of accessory which can be key-bound (determined by the server config)
     * @return a list of {@link AccessoryType}s which can be key-bound
     */
    // todo: cache
    public static ImmutableList<AccessoryType> getKeyboundSlotTypes() {
        ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

        for (String id : OhmegaConfig.Server.keyboundSlotTypes()) {
            builder.add(AccessoryTypeManager.get(Identifier.parse(id)));
        }

        return builder.build().asList();
    }

    /**
     * You should most likely use {@link #getBindTooltip(ItemStack)} as it is easier
     * and uses standardised key formats that work with {@link com.swacky.ohmega.api.datagen.client.OhmegaLangHelper}
     * <p>
     * A utility method used to get a description for key-bound capable accessories
     * @param stack {@link ItemStack} instance of an accessory
     * @param bindKey the translatable key for use when a key-bind is applicable for this stack,
     * use '%s' for the bind key replacement in your translation
     * @param nonBindKey the translatable key for use when a key-bind is not applicable for this stack
     * @return example: "Press G to toggle flight", "Allows the wearer to fly"
     */
    public static MutableComponent getBindTooltip(ItemStack stack, String bindKey, String nonBindKey) {
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

        IAccessory accessory = getAccessory(stack.getItem());
        boolean flag = false;

        if (accessory != null) {
            for (AccessoryType type0 : getKeyboundSlotTypes()) {
                if (slotTypes.contains(type0)) {
                    flag = true;
                    break;
                }
            }
        }

        KeyMapping mapping = OhmegaBinds.getMapping(type, typeIndex);

        if (slot < 0 || !flag || mapping == null) {
            return Component.translatable(nonBindKey).withStyle(ChatFormatting.GRAY);
        }

        return Component.translatable(bindKey, mapping.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY);
    }

    /**
     * A shortcut method to {@link #getBindTooltip(ItemStack, String, String)} that uses standardised key formats
     * that work with {@link com.swacky.ohmega.api.datagen.client.OhmegaLangHelper}
     * @param stack {@link ItemStack} instance of an accessory
     * @return example: "Press G to toggle flight", "Allows the wearer to fly"
     */
    public static MutableComponent getBindTooltip(ItemStack stack) {
        String id = stack.getItem().getDescriptionId();

        return getBindTooltip(stack, id + ".tooltip.keybind", id + ".tooltip");
    }

    /**
     * This is automatically applied internally
     * @param item accessory item
     * @return example: "Accessory Type: Utility"
     */
    public static @Nullable MutableComponent getTypeTooltip(Item item) {
        AccessoryType type = getType(item);

        if (type.displayHoverText()) {
            return Component.translatable("accessory_type", type.getTranslation().getString()).withStyle(ChatFormatting.DARK_GRAY);
        }

        return null;
    }

    /**
     * Finds the first open index of
     * @param player {@link Player} to search the accessory inventory of
     * @param type {@link AccessoryType} of index to find
     * @return index of the first open index matching the type, or {@code -1} if none is found
     */
    public static int getFirstOpenSlot(Player player, AccessoryType type) {
        AccessoryData data = getData(player);
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
     * @param player {@link Player} to equip the accessory on
     * @param stack the right-clicked held {@link ItemStack}
     * @return {@link InteractionResult#SUCCESS} if equipped successfully, else {@link InteractionResult#PASS}
     */
    public static InteractionResult tryEquip(Player player, ItemStack stack) {
        Item item = stack.getItem();
        IAccessory accessory = getAccessory(item);

        if (accessory != null) {
            int slot = getFirstOpenSlot(player, getType(item));

            if (slot >= 0) {
                ItemStack stack0 = stack.copyWithCount(1);

                if (getData(player).setStack(player, slot, stack0, EquipContext.RIGHT_CLICK_HELD_ITEM)) {
                    stack.consume(1, player);
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
    public static boolean compatibleWith(ItemStack first, ItemStack second) {
        return getAccessory(first.getItem()).compatibleWith(second) && getAccessory(second.getItem()).compatibleWith(first);
    }

    /**
     * Checks if an accessory is able to be worn, testing the target accessory {@link ItemStack} against every other worn accessory
     * @param player {@link Player} to get accessory inventory data from
     * @param stack accessory {@link ItemStack} to test against every other accessory currently worn by the player
     * @return {@code true} if the target accessory is compatible with every other worn accessory, {@code false} otherwise
     */

    public static boolean compatibleWith(Player player, ItemStack stack) {
        for (ItemStack other : getAccessoryStacks(player)) {
            if (!compatibleWith(stack, other))  {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieve all of the {@link ItemStack}s in a player's accessory inventory that match a given filter
     * @param player {@link Player} to get accessory inventory data from
     * @param filter A predicate filter to allow or deny elements from the returned list
     * @return every matching {@link ItemStack} in the player's accessory inventory
     */
    public static NonNullList<ItemStack> getStacksFiltered(Player player, Predicate<ItemStack> filter) {
        NonNullList<ItemStack> stacks = getData(player).getStacks();
        NonNullList<ItemStack> filteredStacks = NonNullList.createWithCapacity(stacks.size());

        for (ItemStack stack : stacks) {
            if (filter.test(stack)) {
                filteredStacks.add(stack);
            }
        }

        return filteredStacks;
    }

    /**
     * Retrieve all of the {@link ItemStack}s in a player's accessory inventory that are not empty.
     * @param player {@link Player} to get accessory inventory data from
     * @return every non-empty {@link ItemStack} in the player's accessory inventory
     */
    public static NonNullList<ItemStack> getStacksNoEmpty(Player player) {
        return getStacksFiltered(player, stack -> !stack.isEmpty());
    }

    /**
     * Retrieve all of the {@link ItemStack}s in a player's accessory inventory which are accessories.
     * @param player {@link Player} to get accessory inventory data from
     * @return every {@link ItemStack} in the player's accessory inventory which are accessories
     */
    public static NonNullList<ItemStack> getAccessoryStacks(Player player) {
        return getStacksFiltered(player, stack -> isAccessory(stack.getItem()));
    }

    /**
     * Retrieves all accessories worn by a player in {@link IAccessory} form
     * @param player {@link Player} to get accessory inventory data from
     * @return a nullable list of {@link IAccessory} instances equipped
     */
    public static ArrayList<@Nullable IAccessory> getAccessories(Player player) {
        NonNullList<ItemStack> stacks = getData(player).getStacks();
        ArrayList<IAccessory> accessories = new ArrayList<>(stacks.size());

        for (ItemStack stack : stacks) {
            IAccessory accessory = getAccessory(stack.getItem());

            if (accessory != null) {
                accessories.add(accessory);
            }
        }

        return accessories;
    }

    /**
     * Check if a player is wearing a certain accessory
     * @param player {@link Player} to get accessory inventory data from
     * @param accessory to find
     * @return {@code true} if found, {@code false} otherwise
     */
    public static boolean hasAccessory(Player player, IAccessory accessory) {
        for (ItemStack stack : getData(player).getStacks()) {
            if (getAccessory(stack.getItem()) == accessory) {
                return true;
            }
        }

        return false;
    }

    /**
     * Find the first {@link ItemStack} of a certain item
     * @param player {@link Player} to get accessory inventory data from
     * @param item the item to find
     * @return the found matching {@link ItemStack}, or else {@link ItemStack#EMPTY}
     */
    public static ItemStack getStack(Player player, Item item) {
        for (ItemStack stack : getData(player).getStacks()) {
            if (stack.getItem() == item) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public interface Service {
        AccessoryData getData(Player player);
    }
}
