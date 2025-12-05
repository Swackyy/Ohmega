package com.swacky.ohmega.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.dataattachment.AccessoryInvDataAttachment;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.datacomponent.AccessoryItemDataComponent;
import com.swacky.ohmega.common.init.OhmegaTags;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.CommonEvents;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.ModNetworking;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class AccessoryHelper {
    /**
     * This is an internal function; it is in the {@code api} package as you can use it, however it is discouraged
     * @param stack an {@link ItemStack} instance to retrieve the {@link AccessoryItemDataComponent} from
     * @return the {@link AccessoryItemDataComponent} bound to the {@link ItemStack}
     */
    public static AccessoryItemDataComponent _getInternalData(ItemStack stack) {
        if (stack.has(OhmegaDataComponents.ACCESSORY_ITEM.get())) {
            return stack.get(OhmegaDataComponents.ACCESSORY_ITEM.get());
        }
        return stack.set(OhmegaDataComponents.ACCESSORY_ITEM.get(), new AccessoryItemDataComponent(-1, false, ModifierHolder.EMPTY));
    }

    /**
     * @param player a {@link Player} instance to retrieve data from
     * @return the a wrapper of the capability used for storing accessory inventory data on the {@link Player}
     */
    public static Optional<AccessoryContainer> getContainer(Player player) {
        LazyOptional<AccessoryInvDataAttachment> opt = player.getCapability(Ohmega.ACCESSORIES);

        if (opt.isPresent()) {
            return Optional.of(new AccessoryContainer(player, opt.orElseThrow(NullPointerException::new)));
        }

        return Optional.empty();
    }

    /**
     * Makes any (including vanilla and other mods') {@link Item}(s) into an accessory by binding an {@link IAccessory} instance to it.
     * @param item the registered {@link Item} target
     * @param binding an {@link IAccessory} instance to bind the target to
     * @return true if it can be bound, false if it is already bound
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        return OhmegaCommon.bindAccessory(item, binding);
    }

    /**
     * Checks if an {@link Item} is in any way an accessory, through either implementing {@link IAccessory} or binding with {@link #bindAccessory(Item, IAccessory)}
     * @param item the {@link Item} to test
     * @return  true if an accessory
     */
    public static boolean isItemAccessoryBound(Item item) {
        return OhmegaCommon.isItemAccessoryBound(item);
    }

    /**
     * Retrieves the {@link IAccessory} instance bound to the passed {@link Item} through implementing {@link IAccessory} or binding with {@link #bindAccessory(Item, IAccessory)}
     * @param item the {@link Item} to retrieve the bound {@link IAccessory} instance
     * @return if found, the {@link Item}'s bound {@link IAccessory} instance, else null
     */
    public static IAccessory getBoundAccessory(Item item) {
        return OhmegaCommon.getBoundAccessory(item);
    }

    /**
     * Finds the slot that a given {@link IAccessory} is found
     * @param player the {@link Player} to check for
     * @param acc the {@link IAccessory} to find the slot of
     * @return the slot that the passed {@link IAccessory} was found. If not found, returns -1
     */
    public static int getSlotFor(Player player, IAccessory acc) {
        ArrayList<ItemStack> list = getStacks(player);
        for (int i = 0; i < list.size(); i++) {
            if (acc == getBoundAccessory(list.get(i).getItem())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * A shortcut method to {@link #getSlotFor(Player, IAccessory)}
     * @param player the {@link Player} to check for
     * @param item the accessory to find the slot of
     * @return the slot that the passed accessory was found. If not found, returns -1
     */
    public static int getSlotFor(Player player, Item item) {
        IAccessory acc = getBoundAccessory(item);
        if (acc != null) {
            return getSlotFor(player, acc);
        }
        return -1;
    }

    /**
     * Sets the slot of an accessory {@link ItemStack}
     * <p>
     * Does not place the {@link ItemStack} into an accessory slot, but instead adds slot index data to the {@link ItemStack}
     * @param stack an {@link ItemStack} of an accessory
     * @param slot the index of the slot to set in
     */
    public static void setSlot(ItemStack stack, int slot) {
        AccessoryItemDataComponent data = _getInternalData(stack);
        if (data != null) {
            data.setSlot(slot);
        }
    }

    /**
     * Gets the item's slot in the player's inventory from an {@link ItemStack}'s tag
     * @param stack the {@link ItemStack} to test against
     * @return the slot of the item if the tag is present, -1 otherwise
     * -1 is returned if not present because otherwise it would return 0, the first slot, which messes things up. Using -1 makes it an outlier
     */
    public static int getSlot(ItemStack stack) {
        AccessoryItemDataComponent data = _getInternalData(stack);
        if (data != null) {
            return data.getSlot();
        }
        return -1;
    }

    /**
     * Checks if the player has an accessory
     * @param player the {@link Player} to check for
     * @param acc the {@link IAccessory} to check if the player has it
     * @return true if the {@link Player} has the passed {@link IAccessory} equipped, otherwise false
     */
    public static boolean hasAccessory(Player player, IAccessory acc) {
        return getSlotFor(player, acc) != -1;
    }

    /**
     * A shortcut method to {@link #hasAccessory(Player, IAccessory)}
     * @param player the {@link Player} to check for
     * @param item the accessory to check if the player has it
     * @return true if the {@link Player} has the passed accessory equipped, otherwise false
     */
    public static boolean hasAccessory(Player player, Item item) {
        IAccessory acc = getBoundAccessory(item);
        if (acc != null) {
            return hasAccessory(player, acc);
        }
        return false;
    }

    /**
     * Returns the {@link ItemStack} corresponding to the slot index of the accessory inventory
     * @param player the {@link Player} to get the stacks from
     * @param slot the index of slot, 5 would be the special slot as an example
     * @return the {@link ItemStack} in the slot provided
     */
    public static ItemStack getStackInSlot(Player player, int slot) {
        ItemStack[] stack = new ItemStack[1];
        getContainer(player).ifPresent(a -> stack[0] = a.getStackInSlot(slot));
        return stack[0];
    }

    /**
     * An inner class allowing functions to be called with a {@link Player} and an {@link ItemStack} argument
     */
    @FunctionalInterface
    public interface Inner {
        void apply(Player player, ItemStack stack);
    }

    /**
     * Checks if a player has an accessory and allows for the calling of a function upon it
     * @param player the {@link Player} to check for
     * @param acc checks if the player has this {@link IAccessory} instance
     * @param holder a holder class for a functional interface, allows users to run methods if the accessory is present
     * @return true if the {@link Player} has the passed accessory, false otherwise
     */
    public static boolean runIfPresent(Player player, IAccessory acc, Inner holder) {
        int slot = getSlotFor(player, acc);
        if (slot != -1) {
            holder.apply(player, getStackInSlot(player, slot));
            return true;
        }
        return false;
    }

    /**
     * Checks if a player has an accessory, if true, the accessory will update
     * @param player the player to check for
     * @param acc the {@link IAccessory} to check if the player has it
     * @return true if the player has the passed accessory, false otherwise
     */
    public static boolean updateIfPresent(Player player, IAccessory acc) {
        return runIfPresent(player, acc, acc::update);
    }

    /**
     * Gets all the {@link AccessoryType}s that this accessory is marked as.
     * Consider instead using {@link #getType(Item)}
     * @param item the {@link Item} of the accessory
     * @return a non-duplicate {@link ImmutableList} of all {@link AccessoryType}s bound to the given accessory
     */
    @SuppressWarnings("deprecation")
    public static ImmutableList<AccessoryType> getTypes(Item item) {
        ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

        if (OhmegaConfig.CONFIG_SERVER.noAccessoryTypes.get()) {
            builder.add(AccessoryType.GENERIC.get());
        }

        for (OhmegaTags.TagHolder holder : OhmegaTags.getTags()) {
            if (item.builtInRegistryHolder().is(holder.getTag())) {
                builder.add(holder.getType());
            }
        }

        ImmutableSet<AccessoryType> set = builder.build();
        if (set.isEmpty()) {
            return ImmutableList.of(AccessoryType.NORMAL.get());
        }
        return ImmutableList.copyOf(set);
    }

    /**
     * A shortcut method to {@link #getTypes(Item)}.
     * Consider instead using {@link #getType(ItemStack)}
     * @param stack the {@link ItemStack} instance of the given accessory
     * @return a non-duplicate {@link ImmutableList} of all {@link AccessoryType}s bound to the given accessory
     */
    public static ImmutableList<AccessoryType> getTypes(ItemStack stack) {
        return getTypes(stack.getItem());
    }

    /**
     * Retrieves the accessory's effective {@link AccessoryType} (lowest priority index), or if overridden by the {@link com.swacky.ohmega.api.event.AccessoryOverrideTypesEvent}, then that one
     * @param item the {@link Item} of the accessory
     * @return the {@link AccessoryType} of lowest priority index bound to the given accessory
     */
    @SuppressWarnings("deprecation")
    public static AccessoryType getType(Item item) {
        if (OhmegaConfig.CONFIG_SERVER.noAccessoryTypes.get()) {
            return AccessoryType.GENERIC.get();
        }

        {
            ImmutableMap<Item, AccessoryType> map = CommonEvents.getAccessoryTypeOverrides();
            if (map.containsKey(item)) {
                return map.get(item);
            }
        }

        AccessoryType type = AccessoryType.NORMAL.get();

        for (OhmegaTags.TagHolder holder : OhmegaTags.getTags()) {
            if (item.builtInRegistryHolder().is(holder.getTag())) {
                AccessoryType holderType = holder.getType();
                if (holderType.getPriority() < type.getPriority()) {
                    type = holderType;
                }
            }
        }

        return type;
    }

    /**
     * A shortcut method to {@link #getTypes(Item)}.
     * @return the {@link AccessoryType} of lowest priority index bound to the given accessory
     */
    public static AccessoryType getType(ItemStack stack) {
        return getType(stack.getItem());
    }

    /**
     * Turns config value for slot types into usable data
     * @return an {@link ImmutableList} of {@link AccessoryType}s from the config value
     */
    public static ImmutableList<AccessoryType> getSlotTypes() {
        if (OhmegaConfig.CONFIG_SERVER.noAccessoryTypes.get()) {
            int size = OhmegaConfig.CONFIG_SERVER.slotTypes.get().size();
            ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(size);
            for (int i = 0; i < size; i++) {
                builder.add(AccessoryType.GENERIC.get());
            }
            return builder.build();
        }

        ImmutableList.Builder<AccessoryType> builder = new ImmutableList.Builder<>();
        for (String str : OhmegaConfig.CONFIG_SERVER.slotTypes.get()) {
            builder.add(AccessoryTypeManager.getInstance().get(ResourceLocation.parse(str)));
        }
        return builder.build();
    }

    /**
     * @return an {@link ImmutableList} of {@link String}s of accessory slots' types
     */
    public static ImmutableList<String> getSlotTypesStr() {
        if (OhmegaConfig.CONFIG_SERVER.noAccessoryTypes.get()) {
            int size = OhmegaConfig.CONFIG_SERVER.slotTypes.get().size();
            ImmutableList.Builder<String> builder = ImmutableList.builderWithExpectedSize(size);
            for (int i = 0; i < size; i++) {
                builder.add(AccessoryType.GENERIC.get().getId().toString());
            }
            return builder.build();
        }

        return ImmutableList.copyOf(OhmegaConfig.CONFIG_SERVER.slotTypes.get());
    }

    /**
     * Turns config value for keybound slot types into usable data, filtering out duplicates
     * @return an {@link ImmutableList} of {@link AccessoryType}s from the config value
     */
    public static ImmutableList<AccessoryType> getKeyboundSlotTypes() {
        if (OhmegaConfig.CONFIG_SERVER.noAccessoryTypes.get()) {
            return ImmutableList.of(AccessoryType.GENERIC.get());
        }

        ImmutableList.Builder<AccessoryType> builder = new ImmutableList.Builder<>();
        for (String str : OhmegaConfig.CONFIG_SERVER.keyboundSlotTypes.get()) {
            builder.add(AccessoryTypeManager.getInstance().get(ResourceLocation.parse(str)));
        }
        return ImmutableSet.copyOf(builder.build()).asList();
    }

    /**
     * @return an {@link ImmutableList} of {@link String}s of key-bound accessory slots' types
     */
    @SuppressWarnings("unchecked")
    public static ImmutableList<String> getKeyboundSlotTypesStr() {
        if (OhmegaConfig.CONFIG_SERVER.noAccessoryTypes.get()) {
            return ImmutableList.of(AccessoryType.GENERIC.get().getId().toString());
        }

        return (ImmutableList<String>) ImmutableSet.copyOf(OhmegaConfig.CONFIG_SERVER.keyboundSlotTypes.get()).asList();
    }

    /**
     * A utility method to get a description for a key-bind activated {@link IAccessory}.
     * @param bindDescription what will be displayed for the bind to do / activate. Use "&lt;BIND&gt;" to show the bind letter
     * @param stack a {@link ItemStack} instance of the given accessory
     * @param other the "other" tooltip, for displaying when {@link IAccessory} is not in an accessory slot (such sas in the normal inventory)
     * @return if the slot is a normal category slot: The "other" {@link Component}
     * <p>
     * An example: {@code "Press B to activate invisibility"} is produced by {@code "Press <BIND> to activate invisibility"} when in a slot with key-binding of key B
     */
    public static MutableComponent getBindTooltip(ComponentContents bindDescription, ItemStack stack, ComponentContents other) {
        ImmutableList<AccessoryType> slotTypes = getSlotTypes();

        int slot = getSlot(stack);
        AccessoryType type;
        if (slot < 0 || slot >= slotTypes.size()) {
            type = null;
        } else {
            type = slotTypes.get(slot);
        }

        // Starts at -1 to align properly
        int typeIndex = -1;
        if (type != null) {
            for (int i = 0; i < slot + 1; i++) {
                if (slotTypes.get(i) == type) {
                    typeIndex++;
                }
            }
        }

        boolean flag = false;
        IAccessory acc = getBoundAccessory(stack.getItem());
        if (acc != null) {
            for (AccessoryType type0 : getKeyboundSlotTypes()) {
                if (slotTypes.contains(type0)) {
                    flag = true;
                    break;
                }
            }
        }

        KeyMapping mapping = OhmegaBinds.Generated.getMapping(type, typeIndex);

        if (slot < 0 || !flag || mapping == null) {
            return MutableComponent.create(other).withStyle(ChatFormatting.GRAY);
        } else {
            return MutableComponent.create(new PlainTextContents.LiteralContents(MutableComponent.create(bindDescription).getString().replace("<BIND>", mapping.getTranslatedKeyMessage().getString()))).withStyle(ChatFormatting.GRAY);
        }
    }

    /**
     * A utility method to create a {@link MutableComponent} of the translated {@link AccessoryType} bound to an accessory
     * @param item the {@link Item} of an accessory to get the type from
     * @return a {@link MutableComponent} instance of "Accessory type: TYPE"
     */
    @Nullable
    public static MutableComponent getTypeTooltip(Item item) {
        AccessoryType type = getType(item);
        if (type.displayHoverText()) {
            return Component.translatable("accessory_type", type.getTranslation().getString()).withStyle(ChatFormatting.DARK_GRAY);
        }
        return null;
    }

    /**
     * Utility function to add {@link AttributeModifier}s to a {@link Player} from an {@link ModifierHolder.Builder}
     * @param player add/remove modifiers to/from this {@link Player}
     * @param map a {@link Multimap} which contains the modifiers to add or remove
     * @param add if true, will add the {@link AttributeModifier}(s) to the {@link Player}, otherwise existing ones will be removed
     */
    public static void changeModifiers(Player player, ItemAttributeModifiers map, boolean add) {
        for (ItemAttributeModifiers.Entry entry : map.modifiers()) {
            AttributeInstance attribute0 = player.getAttribute(entry.attribute());
            if (attribute0 != null) {
                if (add) {
                    if (!attribute0.hasModifier(entry.modifier().id())) {
                        attribute0.addTransientModifier(entry.modifier());
                    }
                } else {
                    attribute0.removeModifier(entry.modifier());
                }
            }
        }
    }

    /**
     * Retrieves the {@link ModifierHolder} from an {@link ItemStack} instance
     * @param stack an {@link ItemStack} of an accessory
     * @return the bound {@link ModifierHolder}
     */
    public static ModifierHolder getModifiers(ItemStack stack) {
        AccessoryItemDataComponent data = _getInternalData(stack);
        if (data != null) {
            return data.getModifiers();
        }
        return ModifierHolder.EMPTY;
    }

    /**
     * Removes both passive and active only {@link AttributeModifier}s
     * @param player to remove modifiers from
     * @param holder contains the modifiers to be removed
     */
    public static void removeAllModifiers(Player player, ModifierHolder holder) {
        changeModifiers(player, holder.getPassive(), false);
        changeModifiers(player, holder.getActive(), false);
    }

    /**
     * Adds a tag to show the active state of an {@link IAccessory} item
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link IAccessory} to add the tag to
     * @param value the value of the active state
     * This is handled internally but for whatever reason you want to change the value of this, you can
     */
    public static void setActive(Player player, ItemStack stack, boolean value) {
        if (isItemAccessoryBound(stack.getItem())) {
            AccessoryItemDataComponent data = _getInternalData(stack);
            if (data != null) {
                data.setActive(value);
            }
        }
        changeModifiers(player, getModifiers(stack).getActive(), value);
    }

    /**
     * Checks the active state of an {@link IAccessory} item by tag
     * @param stack the {@link ItemStack} to check the active state of
     * @return true if active, false if inactive or not an {{@link IAccessory} item
     */
    public static boolean isActive(ItemStack stack) {
        if (isItemAccessoryBound(stack.getItem())) {
            AccessoryItemDataComponent data = _getInternalData(stack);
            if (data != null) {
                return data.isActive();
            }
        }
        return false;
    }

    /**
     * Activates an {@link IAccessory} item in a slot
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link ItemStack} to activate an accessory on
     */
    public static void activate(Player player, ItemStack stack) {
        setActive(player, stack, true);
    }

    /**
     * Deactivates an {@link IAccessory} item in a slot
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link ItemStack} to deactivate an accessory on
     */
    public static void deactivate(Player player, ItemStack stack) {
        setActive(player, stack, false);
    }

    /**
     * Toggles an {@link IAccessory}'s active state
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link ItemStack} to toggle an accessory's active state
     */
    public static void toggle(Player player, ItemStack stack) {
        if (isActive(stack)) {
            deactivate(player, stack);
        } else {
            activate(player, stack);
        }
    }

    /**
     * Returns the first open accessory slot for an {@link AccessoryType} or {@link AccessoryType}s
     * @param player the {@link Player} to test their slots against
     * @param type the {@link AccessoryType} to check for the slots
     * @return the first open slot of the type, else if none is found then -1
     */
    public static int getFirstOpenSlot(Player player, AccessoryType type) {
        int[] out = {-1};
        getContainer(player).ifPresent(a -> {
            ImmutableList<AccessoryType> slotTypes = getSlotTypes();
            for (int i = 0; i < a.getSlots(); i++) {
                if (slotTypes.get(i) == type && a.getStackInSlot(i).isEmpty()) {
                    out[0] = i;
                    return;
                }
            }
        });
        return out[0];
    }

    /**
     * A utility method to allow for the equipping of accessories by a right-click
     * * <p>
     * This is automatically called by Ohmega for accessory items, you can disable this through the use of {@link AccessoryEquipEvent}, checking for {@link AccessoryEquipEvent.Context#RIGHT_CLICK_HELD_ITEM}
     * @param player the {@link Player} to put the {@link IAccessory} on
     * @param hand the {@link InteractionHand} to get the accessory held
     * @return an interaction result of success if the item is equipped, else a pass
     */
    public static InteractionResult tryEquip(Player player, InteractionHand hand) {
        InteractionResult[] out = new InteractionResult[]{InteractionResult.PASS};
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        IAccessory acc = getBoundAccessory(item);
        if (acc != null) {
            getContainer(player).ifPresent(a -> {
                int slot = getFirstOpenSlot(player, getType(item));
                if (slot >= 0) {
                    ItemStack stack0 = stack.copyWithCount(1);
                    if (a.setStackInSlot(slot, stack0)) {
                        changeModifiers(player, AccessoryHelper.getModifiers(stack).getPassive(), true);

                        stack.shrink(1);
                        setSlot(stack, slot);

                        if (!OhmegaHooks.accessoryEquipEvent(player, stack0, AccessoryEquipEvent.Context.RIGHT_CLICK_HELD_ITEM)) {
                            acc.onEquip(player, stack0);
                        }
                        if (acc.getEquipSound() != null) {
                            player.playSound(acc.getEquipSound().get(), 1, 1);
                        }
                        out[0] = InteractionResult.SUCCESS;
                    }

                }
            });
        }
        return out[0];
    }

    /**
     * Returns all the stacks from the player's accessory inventory
     * @param player the {@link Player} to get the stacks from
     * @return an {@link ArrayList} of all accessory slot stacks
     */
    public static ArrayList<ItemStack> getStacks(Player player) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        getContainer(player).ifPresent(a -> {
            for (int i = 0; i < a.getSlots(); i++) {
                stacks.add(a.getStackInSlot(i));
            }
        });
        return stacks;
    }

    /**
     * Returns all the accessories from the player's accessory inventory
     * @param player the {@link Player} to get the stacks from
     * @return an {@link ArrayList} of all accessory slot accessories
     */
    public static ArrayList<IAccessory> getAccessories(Player player) {
        ArrayList<IAccessory> accessories = new ArrayList<>();
        for (ItemStack stack : getStacks(player)) {
            IAccessory acc = getBoundAccessory(stack.getItem());
            if (acc != null) {
                accessories.add(acc);
            }
        }
        return accessories;
    }

    /**
     * Checks if two {@link IAccessory} instances are compatible with each other
     * @param acc first {@link IAccessory} instance
     * @param acc0 second {@link IAccessory} instance
     * @return true if both {@link IAccessory} instances are compatible with one another
     */
    public static boolean compatibleWith(IAccessory acc, IAccessory acc0) {
        return acc.checkCompatibility(acc0) && acc0.checkCompatibility(acc);
    }

    /**
     * A shortcut method to {@link #compatibleWith(IAccessory, IAccessory)} with one {@link Item} and one {@link IAccessory} parameter
     * @param item first {@link Item} accessory instance
     * @param acc second {@link IAccessory} instance
     * @return true if both accessories are compatible with one another
     */
    public static boolean compatibleWith(Item item, IAccessory acc) {
        IAccessory acc0 = getBoundAccessory(item);
        if (acc0 != null) {
            return compatibleWith(acc, acc0);
        }
        return false;
    }

    /**
     * A shortcut method to {@link #compatibleWith(IAccessory, IAccessory)} with two {@link Item} parameters
     * @param item first {@link Item} accessory instance
     * @param item0 second {@link Item} accessory instance
     * @return true if both accessories are compatible with one another
     */
    public static boolean compatibleWith(Item item, Item item0) {
        IAccessory acc = getBoundAccessory(item);
        IAccessory acc0 = getBoundAccessory(item0);
        if (acc != null && acc0 != null) {
            return compatibleWith(acc, acc0);
        }
        return false;
    }

    /**
     * Checks compatibility of the {@link IAccessory} provided with all other equipped accessories
     * @param player the {@link Player} to get the accessory inventory of
     * @param acc the {@link IAccessory} to test against
     * @return true if compatible, false if not
     */
    public static boolean compatibleWith(Player player, IAccessory acc) {
        boolean[] out = {true};
        for (IAccessory acc0 : getAccessories(player)) {
            if (!AccessoryHelper.compatibleWith(acc, acc0)) {
                out[0] = false;
            }
        }
        return out[0];
    }

    /**
     * A shortcut method to {@link #compatibleWith(Player, IAccessory)}
     * @param player the {@link Player} to get the accessory inventory of
     * @param item the accessory to test against
     * @return true if compatible, false if not
     */
    public static boolean compatibleWith(Player player, Item item) {
        IAccessory acc = getBoundAccessory(item);
        if (acc != null) {
            return compatibleWith(player, acc);
        }
        return false;
    }

    /**
     * Forcefully notifies the game that a slot has been changed
     * <p>
     * You may need to use this when an {@link ItemStack}'s data has been changed, but {@link AccessoryInvDataAttachment#setStackInSlot(int, ItemStack)} (or similar) has not been called
     * @param player {@link Player} to update the {@link AccessoryInvDataAttachment} of
     * @param slot the index of which to notify changes of
     */
    public static void setSlotChanged(Player player, int slot) {
        getContainer(player).ifPresent(a -> a.onContentsChanged(slot));
    }

    /**
     * Synchronises slots from server data with provided clients
     * @param player a {@link ServerPlayer} to retrieve and send data from
     * @param slots the indexes of the slots to sync
     * @param stacks {@link ItemStack}s from the server, data used to sync
     * @param receivers {@link Player}(s)/client(s) to have server data sent to
     */
    public static void syncSlots(ServerPlayer player, int[] slots, List<ItemStack> stacks, Collection<ServerPlayer> receivers) {
        SyncAccessorySlotsPacket packet = new SyncAccessorySlotsPacket(player.getId(), slots, stacks);
        for (ServerPlayer receiver : receivers) {
            ModNetworking.send(packet, receiver);
        }
    }

    /**
     * Synchronises all slots from server data with provided clients
     * @param player a {@link ServerPlayer} to retrieve and send data from
     * @param receivers {@link Player}(s)/client(s) to have server data sent to
     */
    public static void syncAllSlots(ServerPlayer player, Collection<ServerPlayer> receivers) {
        ArrayList<ItemStack> stacks = getStacks(player);
        int size = stacks.size();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) {
            slots[i] = i;
        }
        syncSlots(player, slots, stacks, receivers);
    }
}