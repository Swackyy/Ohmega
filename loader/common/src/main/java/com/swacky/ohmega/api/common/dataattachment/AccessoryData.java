package com.swacky.ohmega.api.common.dataattachment;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.common.menu.TemporarySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Storage holder for accessory-related data, attachable for any {@link LivingEntity}
 */
public final class AccessoryData {
    public static final @NonNull Codec<AccessoryData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryData::new));

    public static final @NonNull MapCodec<AccessoryData> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryData::new));

    private @NonNull NonNullList<ItemStack> stacks;
    private boolean[] hidden;
    private long tickIndex = 0;

    /**
     * Internal constructor for redirection
     * @param stacks list of {@link ItemStack}s in the slots
     * @param hidden boolean array dictating the visibility of renderable accessories,
     *               not retained through the items themselves, but rather bound to the slot indexes
     */
    private AccessoryData(@NonNull NonNullList<ItemStack> stacks, boolean[] hidden) {
        this.stacks = stacks;
        this.hidden = hidden;
    }

    /**
     * Internal constructor used by {@link Codec}s
     * @param stacks list of {@link ItemStack}s in the slots
     * @param hidden boolean array dictating the visibility of renderable accessories,
     *               not retained through the items themselves, but rather bound to the slot indexes
     */
    private AccessoryData(@NonNull List<ItemStack> stacks, @NonNull List<Boolean> hidden) {
        this(NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0])), Booleans.toArray(hidden));
    }

    /**
     * {@code public}ly exposed constructor, used internally by Ohmega.
     * Initialises {@link ItemStack}s as all empty and all {@code hidden} as {@code false}
     */
    public AccessoryData() {
        int size = AccessoryHelper.getSlotTypes().size();
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.hidden = new boolean[size];
    }

    /**
     * Get the amount of items supported by this storage instance
     * @return the amount of items held
     */
    public int size() {
        return stacks.size();
    }

    /**
     * Retrieve all the stacks held by this accessory extension
     * @return the stored {@link ItemStack}s
     */
    public @NonNull NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    /**
     * Retrieve the {@link ItemStack} in the given slot index
     * @param index slot index relative to the accessory extension
     * @return the {@link ItemStack} in the provided slot index
     */
    public @NonNull ItemStack getStackInSlot(int index) {
        return stacks.get(index);
    }

    /**
     * Retrieve the boolean data representing which indexes' accessories should not be rendered
     * @return the stored hidden array
     */
    public boolean[] getHidden() {
        return hidden;
    }

    /**
     * Check if a particular index's accessory should be hidden
     * @param index slot index relative to the accessory extension
     * @return {@code true} if it is hidden, and accessories in this slot should not be rendered, {@code false} otherwise
     */
    public boolean isHidden(int index) {
        return hidden[index];
    }

    /**
     * Set one index's hidden state, used primarily in synchronisation
     * @param index slot index relative to the accessory extension
     * @param value {@code true} to set it as hidden, {@code false} to set as visible
     */
    public void setHidden(int index, boolean value) {
        hidden[index] = value;
    }

    /**
     * Called internally when attaching this data storage to the target entity, only necessary for players
     * @param player the {@link ServerPlayer} the data is being attached to
     */
    public void onAttach(@NonNull ServerPlayer player) {
        // If the server config gets de-synced, this fixes it instead of throwing
        reload(player);

        int size = size();
        int[] allIndexes = new int[size];

        for (int i = 0; i < size; i++) {
            ItemStack stack = getStackInSlot(i);

            if (AccessoryHelper.isActive(stack)) {
                doEquip(player, stack, i, EquipContext.ATTACH);
            }

            allIndexes[i] = i;
        }

        // Initial load syncing
        syncAllData(player, player.getId(), allIndexes);

        // Rebuild slots for InventoryMenu
        InventoryMenu menu = player.inventoryMenu;
        NonNullList<Slot> slots = menu.slots;
        Slot[] toRemove = new Slot[AccessoryHelper.getSlotTypes().size()];
        int cursor = 0;

        for (Slot slot : slots) {
            if (slot instanceof TemporarySlot) {
                toRemove[cursor++] = slot;
            }
        }

        for (Slot slot : toRemove) {
            slots.remove(slot);
        }

        AccessoryMenus.onConstruct(menu, player);

        if (menu instanceof IAccessoryMenu accessoryMenu) {
            AccessoryMenuExtension extension = accessoryMenu.getAccessoryExtension();

            if (extension != null) {
                List<AccessorySlot> accessorySlots = extension.getSlots();

                for (int i = 0; i < toRemove.length; i++) {
                    int newIndex = toRemove[i].index;
                    AccessorySlot target = accessorySlots.get(i);

                    if (target.index != newIndex) {
                        Slot tempSlot = slots.get(newIndex);

                        slots.set(newIndex, target);
                        slots.set(target.index, tempSlot);

                        target.index = newIndex;
                    }
                }
            }
        }

        menu.sendAllDataToRemote();
    }

    /**
     * Called at the end of the provided entity's {@link Entity#tick()}
     * @param entity the ticking entity to which this storage instance is attached
     */
    public void tick(@NonNull LivingEntity entity) {
        int size = size();

        // Server only variables
        ServerLevel level;
        IntList indexes = null;
        List<ItemStack> stacks = null;

        if (entity.level() instanceof ServerLevel serverLevel) {
            level = serverLevel;
        } else {
            level = null;
        }

        for (int i = 0; i < size; i++) {
            ItemStack stack = getStackInSlot(i);
            Item item = stack.getItem();
            Accessory accessory = Accessories.get(item);

            if (accessory != null) {
                if (accessory.preferInventoryTick(stack)) {
                    if (level != null) {
                        item.inventoryTick(stack, level, entity, null);
                    }
                } else {
                    accessory.accessoryTick(entity, stack);
                }

                if (level != null && accessory.autoSync(stack) && tickIndex > 0) {
                    byte operand = accessory.autoSyncModulo(stack);

                    if (operand != 0 && tickIndex % operand == 0) {
                        if (indexes == null) {
                            indexes = new IntArrayList(size);
                            stacks = new ArrayList<>(size);
                        }

                        indexes.add(i);
                        stacks.add(stack);
                    }
                }
            }
        }

        if (indexes != null) {
            int[] indexesArray = indexes.toIntArray();

            for (ServerPlayer receiver : level.players()) {
                OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entity.getId(), indexesArray, stacks, true));
            }
        }

        tickIndex++;
    }

    /**
     * Toggle one index's hidden state
     * @param entity the entity that this data instance belongs to
     * @param index slot index relative to the accessory extension
     */
    public void toggleHidden(@NonNull LivingEntity entity, int index) {
        if (OhmegaConfig.Server.getData().allowHideAccessories().get()) {
            boolean value = !isHidden(index);

            setHidden(index, value);

            if (entity.level().isClientSide()) {
                OhmegaNetworking.C2S.send(new SetHiddenPacket(index, value));
            }
        }
    }

    /**
     * Predicate function to assert validity of an {@link ItemStack} candidate to place it in a slot
     * @param entity the entity attempting to equip this item / this call is relevant towards
     * @param index slot index relative to the accessory extension
     * @param stack the {@link ItemStack} to check validity for
     * @param context the context of this call
     * @return {@code true} if valid, {@code false} if invalid
     */
    public boolean isItemValid(@NonNull LivingEntity entity, int index, @NonNull ItemStack stack, @NonNull EquipContext context) {
        if (stack.isEmpty()) {
            return true;
        }

        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null && (AccessoryHelper.compatibleWith(entity, stack) || ItemStack.isSameItem(stack, getStackInSlot(index)))) {
            return
                    AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(index) &&
                    accessory.canEquip(entity, stack, context);
        }

        return false;
    }

    /**
     * Perform necessary operations that occur when un-equipping an accessory
     * @param entity the entity un-equipping the accessory
     * @param stack the accessory's {@link ItemStack} representation being un-equipped
     * @param context the context surrounding this un-equip invocation
     */
    public void doUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null) {
            accessory.onUnequip(entity, stack, context);
            AccessoryHelper.changeModifiers(entity, stack.get(DataComponents.ATTRIBUTE_MODIFIERS), true);
            AccessoryHelper.setNoSlot(stack);
        }
    }

    /**
     * Perform necessary operations that occur when un-equipping an accessory
     * @param entity the entity un-equipping the accessory
     * @param stack the accessory's {@link ItemStack} representation being un-equipped
     * @param context the context surrounding this equip invocation
     */
    private void doEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, int index, @NonNull EquipContext context) {
        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null) {
            AccessoryHelper.setSlot(stack, index);
            AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), true);
            AccessoryHelper.changeModifiers(entity, stack.get(DataComponents.ATTRIBUTE_MODIFIERS), true);
            accessory.onEquip(entity, stack, context);

            if (context == EquipContext.USE_HELD) {
                SoundData data = accessory.getEquipSound(stack);

                if (data != null) {
                    entity.playSound(data.sound().value(), data.volume(), data.pitch());
                }
            }
        }
    }

    /**
     * Synchronise the requested data stored on the server with this instance with all clients
     * @param entity the entity that this data instance belongs to
     * @param index the index to synchronise
     */
    public void sendSync(@NonNull LivingEntity entity, int index) {
        sendSync(entity, new int[]{index}, List.of(getStackInSlot(index)));
    }

    /**
     * Synchronise the requested data stored on the server with this instance with all clients
     * @param entity the entity that this data instance belongs to
     * @param index the index to synchronise
     * @param stack the matching {@link ItemStack} to synchronise as, corresponding to the {@code index}
     */
    public void sendSync(@NonNull LivingEntity entity, int index, @NonNull ItemStack stack) {
        sendSync(entity, new int[]{index}, List.of(stack));
    }

    /**
     * Synchronise the requested data stored on the server with this instance with all clients
     * @param entity the entity that this data instance belongs to
     * @param indexes the indexes to synchronise
     * @param stacks the matching {@link ItemStack}s to synchronise as, corresponding to the {@code indexes}
     */
    public void sendSync(@NonNull LivingEntity entity, int[] indexes, @NonNull List<ItemStack> stacks) {
        if (entity.level() instanceof ServerLevel level) {
            for (ServerPlayer receiver : level.players()) {
                OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entity.getId(), indexes, stacks, true));
            }
        }
    }

    /**
     * Perform the actual stack setting and other related operations
     * @param entity the entity that this data instance belongs to
     * @param index slot index relative to the accessory extension to set in
     * @param stack the {@link ItemStack} to set as in the provided slot index
     * @param context the context surrounding this set invocation
     * @param forceOnEquip {@code true} if {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} should be force-called, {@code false} otherwise
     * @param sync {@code true} if this invocation should be synced with clients
     */
    private void doSetStack(@NonNull LivingEntity entity, int index, @NonNull ItemStack stack, @NonNull EquipContext context, boolean forceOnEquip, boolean sync) {
        ItemStack current = getStackInSlot(index);

        if (!ItemStack.matches(current, stack)) {
            doUnequip(entity, current, context);

            if (stack.isEmpty()) {
                AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), false);
            }

            stacks.set(index, stack);

            if (forceOnEquip || AccessoryHelper.isActive(stack)) {
                doEquip(entity, stack, index, context);
            }

            if (sync) {
                sendSync(entity, index, stack);
            }
        }
    }

    /**
     * Set the {@link ItemStack} in a certain index
     * @param entity the entity that this data instance belongs to
     * @param index slot index relative to the accessory extension to set in
     * @param stack the {@link ItemStack} to set as in the provided slot index
     * @param context the context surrounding this set invocation
     * @param bypassValidation {@code true} will not check {@link #isItemValid(LivingEntity, int, ItemStack, EquipContext)} before setting
     * @param forceOnEquip {@code true} if {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} should be force-called, {@code false} otherwise
     * @return {@code true} if successful ({@link #isItemValid(LivingEntity, int, ItemStack, EquipContext)}
     * result if {@code bypassValidation} is {@code false}
     */
    // todo fix: Syncing with this is bugged as it will always call Accessory#onEquip afaik
    // todo update: confirmed, this happens because forceOnEquip = true from AccessorySlot#set, but its effects are only visible on Forge for some reason
    public boolean setStack(@NonNull LivingEntity entity, int index, @NonNull ItemStack stack, @NonNull EquipContext context, boolean bypassValidation, boolean forceOnEquip) {
        if (bypassValidation || isItemValid(entity, index, stack, context)) {
            doSetStack(entity, index, stack, context, forceOnEquip, true);
            return true;
        }

        return false;
    }

    /**
     * Set the {@link ItemStack} in a certain index.
     * This is most likely the overload you want to use
     * @param entity the entity that this data instance belongs to
     * @param index slot index relative to the accessory extension to set in
     * @param stack the {@link ItemStack} to set as in the provided slot index
     * @param context the context surrounding this set invocation
     * @return the result of {@link #isItemValid(LivingEntity, int, ItemStack, EquipContext)}
     */
    public boolean setStack(@NonNull LivingEntity entity, int index, @NonNull ItemStack stack, @NonNull EquipContext context) {
        return setStack(entity, index, stack, context, false, true);
    }

    /**
     * Set multiple {@link ItemStack}s in the provided indexes
     * @param entity the entity that this data instance belongs to
     * @param indexes slot indexes relative to the accessory extension to set in
     * @param stacks the {@link ItemStack}s to set as matching the provided slot indexes
     * @param context the context surrounding this set invocation
     * @param forceOnEquip {@code true} if {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} should be force-called, {@code false} otherwise
     */
    public void setStacks(@NonNull LivingEntity entity, int[] indexes, @NonNull List<ItemStack> stacks, @NonNull EquipContext context, boolean forceOnEquip) {
        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];

            if (index < size()) {
                doSetStack(entity, index, stacks.get(i), context, forceOnEquip, false);
            }
        }

        sendSync(entity, indexes, stacks);
    }

    /**
     * Sets all the {@link ItemStack}s in the given range to the provided new {@link ItemStack}s
     * @param entity the entity that this data instance belongs to
     * @param minIndex minimum index to set in, inclusive
     * @param maxIndex maximum index to set in, exclusive
     * @param allStacks the stacks corresponding to the index range to set as
     * @param context the context surrounding this set invocation
     * @param forceOnEquip {@code true} if {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} should be force-called, {@code false} otherwise
     */
    public void setStacksRange(@NonNull LivingEntity entity, int minIndex, int maxIndex, @NonNull List<ItemStack> allStacks, @NonNull EquipContext context, boolean forceOnEquip) {
        int size = maxIndex - minIndex;
        int[] indexes = new int[size];
        List<ItemStack> stacks = new ArrayList<>(size);
        int cursor = 0;

        for (int i = minIndex; i < maxIndex; i++) {
            indexes[cursor] = i;
            stacks.add(cursor++, allStacks.get(i));
        }

        setStacks(entity, indexes, stacks, context, forceOnEquip);
    }

    /**
     * Remove the {@link ItemStack} from the provided index, returning the removed stack
     * @param entity the entity that this data instance belongs to
     * @param index slot index relative to the accessory extension to take from
     * @param amount the amount to remove
     * @param context the context surrounding this removal invocation
     * @return the removed {@link ItemStack}
     */
    public ItemStack remove(@NonNull LivingEntity entity, int index, int amount, @NonNull EquipContext context) {
        ItemStack stack;

        if (amount < 0) {
            stack = ContainerHelper.takeItem(stacks, index);
        } else {
            stack = ContainerHelper.removeItem(stacks, index, amount);
        }

        if (!ItemStack.isSameItemSameComponents(getStackInSlot(index), stack)) {
            doUnequip(entity, stack, context);
            AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), false);
        }

        return stack;
    }

    /**
     * Attempts to remove the {@link ItemStack} in the given index and return it to the entity's inventory if they are a player,
     * otherwise it will drop in front of them
     * @param entity the entity that this data instance belongs to
     * @param index slot index relative to the accessory extension to take from
     * @param context the context surrounding this removal invocation
     */
    private void removeOrDropStack(@NonNull LivingEntity entity, int index, @NonNull EquipContext context) {
        ItemStack stack = getStackInSlot(index);

        if (!stack.isEmpty()) {
            doUnequip(entity, stack, context);

            if (!(entity instanceof Player player) || !player.addItem(stack)) {
                entity.drop(stack, false, true);
            }
        }
    }

    /**
     * Remove all items matching a given filter up to a maximum amount
     * @param entity the entity that this data instance belongs to
     * @param filter the predicate to test against every {@link ItemStack} removal candidate
     * @param max the maximum number of items to remove (cumulative)
     * @param context the context surrounding this clear invocation
     * @return the total number of items cleared
     */
    public int clearMatchingItems(@NonNull LivingEntity entity, @NonNull Predicate<ItemStack> filter, int max, @NonNull EquipContext context) {
        int removed = 0;
        IntList indexes = new IntArrayList();

        for (int i = 0; i < size() && (max < 0 || removed < max); i++) {
            ItemStack stack = getStackInSlot(i);

            if (filter.test(stack)) {
                int count = stack.count();
                int toRemoveCurrentStack;

                if (max < 0) {
                    toRemoveCurrentStack = count;
                } else {
                    toRemoveCurrentStack = Math.clamp(max - removed, 0, count);
                }

                if (max < 0 || removed + count <= max) {
                    doUnequip(entity, stack, context);
                    indexes.add(i);
                }

                stack.shrink(toRemoveCurrentStack);

                removed += toRemoveCurrentStack;
            }
        }

        sendSync(entity, indexes.toIntArray(), NonNullList.withSize(indexes.size(), ItemStack.EMPTY));
        return removed;
    }

    /**
     * Synchronise specified data stored in this instance to the given client receiver
     * @param receiver the player to synchronise with
     * @param entityId the ID of the entity which this data instance belongs to
     * @param indexes the slot indexes for which to synchronise data
     */
    private void syncAllData(@NonNull ServerPlayer receiver, int entityId, int[] indexes) {
        OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(entityId, indexes, hidden));
        OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entityId, indexes, stacks, true));
    }

    /**
     * Synchronise all data stored in this instance to the given client receiver
     * @param receiver the player to synchronise with
     * @param entityId the ID of the entity which this data instance belongs to
     */
    public void syncAllData(@NonNull ServerPlayer receiver, int entityId) {
        int size = size();
        int[] allIndexes = new int[size];

        for (int i = 0; i < size; i++) {
            allIndexes[i] = i;
        }

        syncAllData(receiver, entityId, allIndexes);
    }

    /**
     * Mirrors another {@link AccessoryData}'s stored data in this instance, this does not call any {@link IAccessory} methods.
     * This is a shallow copy, use {@link #copyFrom(AccessoryData)} for a deep copy
     * @param other the other instance to mirror
     */
    public void mirror(@NonNull AccessoryData other) {
        stacks = other.getStacks();
        hidden = other.getHidden();
    }

    /**
     * Copy another {@link AccessoryData}'s stored data into this instance, this does not call any {@link IAccessory} methods.
     * This is a deep copy, use {@link #mirror(AccessoryData)} for a shallow copy
     * @param other the other instance to copy from
     */
    public void copyFrom(@NonNull AccessoryData other) {
        int size = other.size();
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        hidden = ArrayUtils.clone(other.getHidden());

        for (int i = 0; i < size; i++) {
            stacks.set(i, other.getStackInSlot(i).copy());
        }
    }

    /**
     * Reloads and rebuilds the data if necessary in accordance to the server config
     * @param entity the entity that this data instance belongs to
     */
    public void reload(@NonNull LivingEntity entity) {
        int oldSize = size();
        ImmutableList<AccessoryType> types = AccessoryHelper.getSlotTypes();
        int newSize = types.size();

        if (newSize > oldSize) {
            // Grow data
            ItemStack[] emptyStackArray = new ItemStack[newSize - oldSize];
            Arrays.fill(emptyStackArray, ItemStack.EMPTY);

            stacks = NonNullList.of(ItemStack.EMPTY, ArrayUtils.addAll(stacks.toArray(new ItemStack[0]), emptyStackArray));
            hidden = ArrayUtils.addAll(hidden, new boolean[newSize - oldSize]);
        } else if (newSize < oldSize) {
            // Drop stacks outside of range
            for (int i = newSize; i < oldSize; i++) {
                removeOrDropStack(entity, i, EquipContext.RESIZE);
            }

            // Shrink data
            stacks = NonNullList.of(ItemStack.EMPTY, Arrays.copyOfRange(stacks.toArray(new ItemStack[0]), 0, newSize));
            hidden = Arrays.copyOfRange(hidden, 0, newSize);
        }

        // Drop invalid stacks (mismatched accessory types and non-accessory items)
        for (int i = 0; i < size(); i++) {
            if (types.get(i) != AccessoryHelper.getType(getStackInSlot(i).getItem())) {
                removeOrDropStack(entity, i, EquipContext.RESIZE);
            }
        }
    }
}
