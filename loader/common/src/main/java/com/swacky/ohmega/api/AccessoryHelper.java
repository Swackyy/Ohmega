package com.swacky.ohmega.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.datacomponent.AccessoryItemDataComponent;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaTags;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public final class AccessoryHelper {
    private static final Service IMPL = OhmegaCommon.loadService(Service.class);

    public static void bootstrap() {}

    /**
     * Retrieves the built-in data stored on each accessory type,
     * you should most likely use wrappers here such as {@link #isActive(ItemStack)} and {@link #setActive(Player, ItemStack, boolean)}
     * instead of accessing and modifying the data here directly
     * @param stack the {@link ItemStack} to retrieve the data from
     * @return the default data component stored on accessory items (when requested)
     */
    public static @Nullable AccessoryItemDataComponent getItemData(ItemStack stack) {
        DataComponentType<AccessoryItemDataComponent> type = OhmegaDataComponents.getItemDataComponent();

        if (stack.has(type)) {
            return stack.get(type);
        }

        if (isItemAccessoryBound(stack.getItem())) {
            stack.set(type, new AccessoryItemDataComponent());
            return stack.get(type);
        }

        return null;
    }

    /**
     * Retrieves the data on the {@link Player} pertaining to worn accessories
     * @param player user to retrieve data from
     * @return the accessory inventory data in the form of an {@link AccessoryContainer}
     */
    public static AccessoryContainer getContainer(Player player) {
        return IMPL.getContainer(player);
    }

    /**
     * Use this to bind an {@link IAccessory} instance to an {@link Item}, essentially turning it into an accessory.
     * If you have read access on the type of item (if the item class is yours), instead implement the {@link IAccessory} interface
     * @param item item to bind
     * @param binding the {@link IAccessory} instance to store on the item, determining most accessory behaviour
     * @return {@code true} if it worked, false if the item is already bound or is air
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        return OhmegaCommon.bindAccessory(item, binding);
    }

    /**
     * Check if an item is registered as an accessory, either by implementing {@link IAccessory} in your {@link Item} class
     * or by calling {@link #bindAccessory(Item, IAccessory)},
     * @param item the item to check if it is bound
     * @return {@code true} if the {@link Item} class implements {@link IAccessory} or is accessory bound by code ({@link #bindAccessory(Item, IAccessory)}
     */
    public static boolean isItemAccessoryBound(Item item) {
        return OhmegaCommon.isItemAccessoryBound(item);
    }

    /**
     * @param item the item to get the binding of
     * @return the {@link IAccessory} binding
     */
    public static IAccessory getBoundAccessory(Item item) {
        return OhmegaCommon.getBoundAccessory(item);
    }

    /**
     * You most likely want to use {@link #getType(Item)} instead
     * @param item the item to find the {@link AccessoryType}s of
     * @return all of the {@link AccessoryType}s that the {@link Item} is a part of,
     * ignoring the priority index of each type
     */
    @SuppressWarnings("deprecation")
    public static ImmutableList<AccessoryType> getTypes(Item item) {
        ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

        if (OhmegaConfig.Server.disableAccessoryTypes()) {
            builder.add(AccessoryType.GENERIC.get());
        }

        AccessoryType override = OhmegaCommon.getTypeOverride(item);

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
     * @return the {@link AccessoryType} of lowest priority index bound to the given accessory
     */
    @SuppressWarnings("deprecation")
    public static AccessoryType getType(Item item) {
        if (OhmegaConfig.Server.disableAccessoryTypes()) {
            return AccessoryType.GENERIC.get();
        }

        AccessoryType override = OhmegaCommon.getTypeOverride(item);

        if (override != null) {
            return override;
        }

        AccessoryType type = null;

        for (Map.Entry<AccessoryType, TagKey<Item>> entry : OhmegaTags.getTags().entrySet()) {
            if (item.builtInRegistryHolder().is(entry.getValue())) {
                AccessoryType candidate = entry.getKey();

                if (type == null || candidate.getPriority() < type.getPriority()) {
                    type = candidate;
                }
            }
        }

        return type;
    }

    /**
     * Retrieves the known slot index of an accessory
     * @param stack {@link ItemStack} to seek data from
     * @return the slot index when equipped, or {@code -1} when not equipped
     */
    public static int getSlot(ItemStack stack) {
        AccessoryItemDataComponent data = getItemData(stack);

        if (data != null) {
            return data.getSlot();
        }

        return -1;
    }

    /**
     * This is handled internally, you most likely won't have to use this
     * @param stack {@link ItemStack} to set slot index of
     * @param slot the index to set to
     */
    public static void setSlot(ItemStack stack, int slot) {
        AccessoryItemDataComponent data = getItemData(stack);

        if (data != null) {
            data.setSlot(slot);
        }
    }

    /**
     * This is handled internally, you most likely won't have to use this
     * <p>
     * Sets the index of the slot to {@code -1}
     * @param stack {@link ItemStack} to set slot index of
     */
    public static void setNoSlot(ItemStack stack) {
        setSlot(stack, -1);
    }

    /**
     * Checks the active state of an accessory item
     * @param stack {@link ItemStack} to seek active state from
     * @return {@code true} if active, {@code false} if inactive
     */
    public static boolean isActive(ItemStack stack) {
        AccessoryItemDataComponent data = getItemData(stack);

        if (data != null) {
            return data.isActive();
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
        AccessoryItemDataComponent data = getItemData(stack);

        if (data != null) {
            data.setActive(value);
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
     * Retrieves the {@link AccessoryModifiers} from an accessory
     * @param stack {@link ItemStack} of an accessory to find the modifiers of
     * @return the {@link AccessoryModifiers} to apply when accessory equipped
     */
    public static AccessoryModifiers getModifiers(ItemStack stack) {
        IAccessory accessory = getBoundAccessory(stack.getItem());
        AccessoryModifiers.Builder builder = new AccessoryModifiers.Builder();

        if (accessory != null) {
            accessory.addDefaultAttributeModifiers(builder);
            OhmegaHooks.accessoryAttributeModifiersEvent(stack, builder);
        }

        return builder.build();
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
     * Retrieves the slot types of each accessory slot in players' accessory inventories (determined by the server config)
     * @return a list of {@link AccessoryType}s matching indexes of accessory indexes
     */
    public static ImmutableList<AccessoryType> getSlotTypes() {
        int size = OhmegaConfig.Server.slotTypes().size();
        ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(size);

        if (OhmegaConfig.Server.disableAccessoryTypes()) {

            for (int i = 0; i < size; i++) {
                builder.add(AccessoryType.GENERIC.get());
            }

            return builder.build();
        }

        for (String id : OhmegaConfig.Server.slotTypes()) {
            builder.add(AccessoryTypeManager.getInstance().get(ResourceLocation.parse(id)));
        }

        return builder.build();
    }

    /**
     * Retrieves the types of accessory which can be key-bound (determined by the server config)
     * @return a list of {@link AccessoryType}s which can be key-bound
     */
    public static ImmutableList<AccessoryType> getKeyboundSlotTypes() {
        ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

        for (String id : OhmegaConfig.Server.keyboundSlotTypes()) {
            builder.add(AccessoryTypeManager.getInstance().get(ResourceLocation.parse(id)));
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

        IAccessory accessory = getBoundAccessory(stack.getItem());
        boolean flag = false;

        if (accessory != null) {
            for (AccessoryType type0 : getKeyboundSlotTypes()) {
                if (slotTypes.contains(type0)) {
                    flag = true;
                    break;
                }
            }
        }

        KeyMapping mapping = OhmegaBinds.Generated.getMapping(type, typeIndex);

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
     * Finds the first open slot of
     * @param player {@link Player} to search the accessory inventory of
     * @param type {@link AccessoryType} of slot to find
     * @return index of the first open slot matching the type, or {@code -1} if none is found
     */
    public static int getFirstOpenSlot(Player player, AccessoryType type) {
        AccessoryContainer container = getContainer(player);
        ImmutableList<AccessoryType> slotTypes = getSlotTypes();

        for (int i = 0; i < container.getSlots(); i++) {
            if (slotTypes.get(i) == type && container.getStackInSlot(i).isEmpty()) {
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
     * @param hand the used {@link InteractionHand} to get the held item from
     * @return Holder for {@link InteractionResult#SUCCESS} if equipped successfully, else {@link InteractionResult#PASS}
     */
    public static InteractionResultHolder<ItemStack> tryEquip(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        IAccessory accessory = getBoundAccessory(item);

        if (accessory != null) {
            int slot = getFirstOpenSlot(player, getType(item));

            if (slot >= 0) {
                ItemStack stack0 = stack.copyWithCount(1);

                if (getContainer(player).setStackInSlot(player, slot, stack0, EquipContext.RIGHT_CLICK_HELD_ITEM)) {
                    stack.shrink(1);

                    if (accessory.getEquipSound() != null) {
                        player.playSound(accessory.getEquipSound().value(), 1, 1);
                    }

                    return InteractionResultHolder.sidedSuccess(stack, player.level().isClientSide());
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    /**
     * Will be removed by {@code v1.6.0} with a method accepting {@link ItemStack}s
     * <p>
     * Checks if two accessories are compatible with each other by testing both ways
     * @param first one accessory
     * @param second a second accessory
     * @return {@code true} if both are compatible with each other, {@code false} otherwise
     */
    @Deprecated(forRemoval = true, since = "1.5.4")
    public static boolean compatibleWith(IAccessory first, IAccessory second) {
        if (first != null && second != null) {
            return first.compatibleWith(second) && second.compatibleWith(first);
        }

        return false;
    }

    /**
     * Will be removed by {@code v1.6.0} with a method accepting an {@link ItemStack}
     * <p>
     * Checks if an accessory is able to be worn, testing the target {@link IAccessory} against every other worn accessory
     * @param player {@link Player} to get accessory inventory data from
     * @param accessory {@link IAccessory} to test against every other accessory currently worn by the player
     * @return {@code true} if the target accessory is compatible with every other worn accessory, {@code false} otherwise
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(since = "1.5.4")
    public static boolean compatibleWith(Player player, IAccessory accessory) {
        for (IAccessory other : getAccessories(player)) {
            if (!compatibleWith(accessory, other))  {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieve all of the {@link ItemStack}s in a player's accessory inventory
     * @param player {@link Player} to get accessory inventory data from
     * @return every {@link ItemStack} in the player's accessory inventory
     */
    public static NonNullList<ItemStack> getStacks(Player player) {
        return getContainer(player).getStacks();
    }

    /**
     * Retrieves all accessories worn by a player
     * @param player {@link Player} to get accessory inventory data from
     * @return a nullable list of {@link IAccessory} instances equipped
     */
    public static ArrayList<@Nullable IAccessory> getAccessories(Player player) {
        NonNullList<ItemStack> stacks = getStacks(player);
        ArrayList<IAccessory> accessories = new ArrayList<>(stacks.size());

        for (ItemStack stack : stacks) {
            IAccessory accessory = getBoundAccessory(stack.getItem());

            if (accessory != null) {
                accessories.add(accessory);
            }
        }

        return accessories;
    }

    /**
     * Synchronises the given indexes' held items to the given {@link Player}s
     * @param player {@link Player} who owns the accessory inventory to synchronise
     * @param slots an array of indexes to synchronise
     * @param stacks a list of {@link ItemStack}s to set the indexes to, matches the indexes provided by the 'indexes' parameter
     * @param receivers {@link Player}s to send the synchronisation packets to
     */
    public static void syncSlots(ServerPlayer player, int[] slots, List<ItemStack> stacks, Collection<ServerPlayer> receivers) {
        for (ServerPlayer receiver : receivers) {
            OhmegaNetworking.S2C.send(receiver, new SyncAccessorySlotsPacket(player.getId(), slots, stacks));
        }
    }

    /**
     * Synchronises all indexes' held items to the given {@link Player}s
     * @param player {@link Player} who owns the accessory inventory to synchronise
     * @param receivers {@link Player}s to send the synchronisation packets to
     */
    public static void syncAllSlots(ServerPlayer player, Set<ServerPlayer> receivers) {
        NonNullList<ItemStack> stacks = getStacks(player);
        int size = stacks.size();
        int[] slots = new int[size];

        for (int i = 0; i < size; i++) {
            slots[i] = i;
        }

        syncSlots(player, slots, stacks, receivers);
    }

    public interface Service {
        AccessoryContainer getContainer(Player player);
    }
}
