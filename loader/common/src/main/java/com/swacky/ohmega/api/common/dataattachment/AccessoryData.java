package com.swacky.ohmega.api.common.dataattachment;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncDataPacket;
import com.swacky.ohmega.network.S2C.SyncSlotsPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Storage holder for accessory-related data, attachable for any {@link LivingEntity}
 */
public final class AccessoryData {
    public static final @NonNull HashSet<LivingEntity> DEFAULT_TRACKERS = new HashSet<>();

    public static final @NonNull MapCodec<AccessoryData> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Codec.BOOL.fieldOf("trackingDefault").forGetter(AccessoryData::isTrackingDefault),
            AccessoryDataEntry.ARRAY_LIST_CODEC.fieldOf("entries").forGetter(AccessoryData::getEntries)
    ).apply(builder, AccessoryData::new));

    public static final @NonNull Codec<AccessoryData> CODEC = MAP_CODEC.codec();

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessoryData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AccessoryData::isTrackingDefault,
            AccessoryDataEntry.ARRAY_LIST_STREAM_CODEC, AccessoryData::getEntries,
            AccessoryData::new);

    private MutableBoolean trackingDefault;
    private @NonNull ArrayList<@NonNull AccessoryDataEntry> entries;
    private long tickIndex = 0;
    private ImmutableList<AccessoryType> typesCache;

    /**
     * Root constructor, used internally
     * @param entries a list of data entries containing all related information corresponding to each slot
     */
    public AccessoryData(boolean trackingDefault, @NonNull ArrayList<@NonNull AccessoryDataEntry> entries) {
        this.trackingDefault = new MutableBoolean(trackingDefault);
        this.entries = entries;

        ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(entries.size());

        for (AccessoryDataEntry entry : entries) {
            builder.add(entry.getType());
        }

        this.typesCache = builder.build();
    }

    /**
     * Publicly exposed constructor, used internally by Ohmega.
     * All data will be instantiated using their default values, or in more detail:
     * <ul>
     *     <li>Tracking default slots</li>
     *     <li>Default initialised {@link AccessoryDataEntry} entries</li>
     * </ul>
     */
    public AccessoryData() {
        List<AccessoryType> types = OhmegaConfig.Server.getDefaultSlotTypes();
        int size = types.size();
        ArrayList<AccessoryDataEntry> entries = new ArrayList<>(size);

        for (AccessoryType type : types) {
            entries.add(new AccessoryDataEntry(type));
        }

        this(true, entries);
    }

    /**
     * Get the amount of items supported by this storage instance
     * @return the number of {@link AccessoryDataEntry} held
     */
    public int size() {
        return entries.size();
    }

    /**
     * Check if this instance holds no entries
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Get a specific entry at the given instance
     * @param index slot index for the desired entry
     * @return the {@link AccessoryDataEntry} at the given {@code index}
     */
    public @NonNull AccessoryDataEntry getEntry(int index) {
        return entries.get(index);
    }

    /**
     * Retrieve all stored entries
     * @return all {@link AccessoryDataEntry} instances held by this data instance
     */
    public @NonNull ArrayList<AccessoryDataEntry> getEntries() {
        return entries;
    }

    /**
     * Retrieve the cached list of {@link AccessoryType}s, rebuilt when slots are changed automatically
     * @return the cached {@link AccessoryType}s held by the {@link #entries}
     */
    public ImmutableList<AccessoryType> getTypes() {
        return typesCache;
    }

    /**
     * Called internally when attaching this data storage to the target entity, only necessary for players
     * @param entity the {@link LivingEntity} the data is being attached to
     */
    public void onAttach(@NonNull LivingEntity entity) {
        int size = size();

        for (int i = 0; i < size; i++) {
            AccessoryDataEntry entry = entries.get(i);
            ItemStack stack = entry.getStack();

            if (OhmegaDataComponents.isActive(stack)) {
                entry.doEquip(entity, stack, i, EquipContext.ATTACH);
            }
        }

        if (!entity.level().isClientSide()) {
            if (isTrackingDefault()) {
                DEFAULT_TRACKERS.add(entity);

                if (!typesCache.equals(OhmegaConfig.Server.getDefaultSlotTypes())) {
                    defaultSlots(entity, EquipContext.ATTACH);
                }
            }

            if (entity instanceof ServerPlayer player) {
                SyncDataPacket packet = new SyncDataPacket(player.getId(), this);

                OhmegaNetworking.sendS2C(player, packet);
            }
        }
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
            ItemStack stack = entries.get(i).getStack();
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
            trySendPacketToAll(level, new SyncStacksPacket(entity.getId(), indexes.toIntArray(), stacks, true));
        }

        tickIndex++;
    }

    /**
     * Synchronise the requested data stored on the server with this instance with all clients
     * @param entity the entity that this data instance belongs to
     * @param indexes the indexes to synchronise
     * @param stacks the matching {@link ItemStack}s to synchronise as, corresponding to the {@code indexes}
     */
    public void trySendSync(@NonNull LivingEntity entity, int[] indexes, @NonNull List<ItemStack> stacks) {
        if (entity.level() instanceof ServerLevel level) {
            for (ServerPlayer receiver : level.players()) {
                OhmegaNetworking.sendS2C(receiver, new SyncStacksPacket(entity.getId(), indexes, stacks, true));
            }
        }
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
        int size = indexes.length;

        for (int i = 0; i < size; i++) {
            int index = indexes[i];

            if (index < size()) {
                entries.get(index).setStack(entity, stacks.get(i), index, context, true, forceOnEquip);
            }
        }

        trySendSync(entity, indexes, stacks);
    }

    /**
     * Sets all the {@link ItemStack}s in the given range to the provided new {@link ItemStack}s
     * @param entity the entity that this data instance belongs to
     * @param minIndex minimum index to set in
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
     * Remove all items matching a given filter up to a maximum amount
     * @param entity the entity that this data instance belongs to
     * @param filter the predicate to test against every {@link ItemStack} removal candidate
     * @param max the maximum number of items to remove (cumulative)
     * @param context the context surrounding this clear invocation
     * @return the total number of items cleared
     */
    public int clearMatchingItems(@NonNull LivingEntity entity, @Nullable Predicate<ItemStack> filter, int max, @NonNull EquipContext context) {
        int size = size();
        int removed = 0;
        IntList indexes = new IntArrayList();

        for (int i = 0; i < size && (max < 0 || removed < max); i++) {
            AccessoryDataEntry entry = entries.get(i);
            ItemStack stack = entry.getStack();

            if (filter == null || filter.test(stack)) {
                int count = stack.count();
                int toRemoveCurrentStack;

                if (max < 0) {
                    toRemoveCurrentStack = count;
                } else {
                    toRemoveCurrentStack = Math.clamp(max - removed, 0, count);
                }

                if (max < 0 || removed + count <= max) {
                    AccessoryDataEntry.doUnequip(entity, stack, context);
                    indexes.add(i);
                }

                stack.shrink(toRemoveCurrentStack);

                removed += toRemoveCurrentStack;
            }
        }

        int count = indexes.size();
        List<ItemStack> stacks = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            stacks.add(ItemStack.EMPTY);
        }

        trySendSync(entity, indexes.toIntArray(), stacks);
        return removed;
    }

    /**
     * Mirrors another {@link AccessoryData}'s stored data with this instance, this does not call any {@link IAccessory} methods.
     * This is a shallow copy, use {@link #copyFrom(AccessoryData, boolean)} for a deep copy
     * @param other the other instance to mirror with
     */
    public void mirror(@NonNull AccessoryData other) {
        trackingDefault = other.trackingDefault;
        entries = other.entries;
    }

    /**
     * Copies from another {@link AccessoryData}'s stored data into this instance, this does not call any {@link IAccessory} methods.
     * This is a deep copy, use {@link #mirror(AccessoryData)} for a shallow copy
     * @param other the other instance to copy from
     */
    public void copyFrom(@NonNull AccessoryData other, boolean slotsOnly) {
        trackingDefault.setValue(other.trackingDefault.booleanValue());
        ArrayList<@NonNull AccessoryDataEntry> otherEntries = other.entries;

        int size = otherEntries.size();
        entries = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            if (slotsOnly) {
                entries.add(new AccessoryDataEntry(otherEntries.get(i).getType()));
            } else {
                entries.add(otherEntries.get(i).copy());
            }
        }
    }

    /**
     * Checks whether this data instance is tracking the default accessory slots
     * @return {@code true} if we are tracking the default accessory slots, {@code false} otherwise
     */
    public boolean isTrackingDefault() {
        return trackingDefault.booleanValue();
    }

    /**
     * Untracks this data instance from the default accessory slots, making it change independently to them
     * @param entity the {@link LivingEntity} this data instance is attached to
     */
    public void untrackDefault(LivingEntity entity) {
        DEFAULT_TRACKERS.remove(entity);
        trackingDefault.setFalse();
    }

    /**
     * Fixes the {@link OhmegaDataComponents#getSlotIndex()} data component for stored items after they may have been moved
     */
    private void resetSlotDataComponents() {
        int size = size();

        for (int i = 0; i < size; i++) {
            entries.get(i).getStack().set(OhmegaDataComponents.getSlotIndex(), i);
        }
    }

    /**
     * Performs operations needed when slots are changed, including rebuilding caches and UI slots
     * @param entity the {@link LivingEntity} this data instance is attached to
     */
    private void onMutateSlots(LivingEntity entity) {
        ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(entries.size());

        for (AccessoryDataEntry entry : entries) {
            builder.add(entry.getType());
        }

        typesCache = builder.build();

        AccessoryMenus.tryRebuildSlots(entity);
    }

    /**
     * Attempts to send the given packet to all players on a {@link Level}, only succeeds if it is a {@link ServerLevel}
     * @param level possibly {@code null} level to retrieve {@link Player} receivers from
     * @param packet the packet to send to each receiver
     */
    private void trySendPacketToAll(@Nullable Level level, @NonNull CustomPacketPayload packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                OhmegaNetworking.sendS2C(player, packet);
            }
        }
    }

    /**
     * Removes slots from this data instance matching an optionally provided filter and up to a given maximum
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param filter the filter for removals, or {@code null} to not filter by type
     * @param max the maximum amount of slots to remove, or {@code -1} to unlimit it
     * @param context the context surrounding this invocation
     * @return the number of slots cleared
     * @apiNote This traverses the {@link #entries} list and subsequently removes entries in reverse order
     */
    public int clearSlots(@NonNull LivingEntity entity, @Nullable Predicate<AccessoryType> filter, int max, @NonNull EquipContext context) {
        untrackDefault(entity);

        int count;
        IntArrayList list = new IntArrayList();
        SyncSlotsPacket packet;

        if (filter == null) {
            if (max < 0) {
                count = size();

                for (AccessoryDataEntry entry : entries) {
                    entry.moveOrDropStack(entity, context);
                }

                entries.clear();

                packet = new SyncSlotsPacket(
                        SyncSlotsPacket.Action.CLEAR_ALL,
                        entity.getId(),
                        ArrayUtils.EMPTY_INT_ARRAY,
                        Optional.empty(),
                        context);
            } else {
                count = 0;

                for (int i = size() - 1; i >= 0 && count < max; i++) {
                    count++;

                    entries.remove(i).moveOrDropStack(entity, context);
                }

                resetSlotDataComponents();

                packet = new SyncSlotsPacket(
                        SyncSlotsPacket.Action.CLEAR,
                        entity.getId(),
                        new int[]{max},
                        Optional.empty(),
                        context);
            }
        } else {
            count = 0;

            for (int i = size() - 1; i >= 0 && (count < max || max < 0); i--) {
                if (filter.test(entries.get(i).getType())) {
                    count++;

                    entries.remove(i).moveOrDropStack(entity, context);
                    list.add(i);
                }
            }

            resetSlotDataComponents();

            packet = new SyncSlotsPacket(
                    SyncSlotsPacket.Action.REMOVE,
                    entity.getId(),
                    list.toIntArray(),
                    Optional.empty(),
                    context);
        }

        onMutateSlots(entity);
        trySendPacketToAll(entity.level(), packet);
        return count;
    }

    /**
     * Resets the slots to the default value (controlled by server config) and enables default slot tracking
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param context the context surrounding this invocation
     */
    public void defaultSlots(@NonNull LivingEntity entity, @NonNull EquipContext context) {
        trackingDefault.setTrue();

        if (!entity.level().isClientSide()) {
            DEFAULT_TRACKERS.add(entity);
        }

        AccessoryData newData = new AccessoryData();
        int newSize = newData.size();
        int size = size();

        for (int i = 0; i < newSize; i++) {
            AccessoryDataEntry newEntry = newData.getEntry(i);

            if (i < size) {
                entries.get(i).setType(entity, newEntry.getType(), context);
            } else {
                entries.add(newEntry);
            }
        }

        for (int i = size() - 1; i >= newSize; i--) {
            entries.remove(i).moveOrDropStack(entity, context);
        }

        onMutateSlots(entity);
        trySendPacketToAll(entity.level(), new SyncSlotsPacket(
                SyncSlotsPacket.Action.DEFAULT,
                entity.getId(),
                ArrayUtils.EMPTY_INT_ARRAY,
                Optional.empty(),
                context));
    }

    /**
     * Inherits accessory slots between a given range from another {@link LivingEntity}, does not track
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param other the other {@link LivingEntity} to inherit slots from
     * @param min the minimum slot index to inherit from
     * @param max the maximum slot index to inherit from, exclusive, or {@code -1} to unlimit
     * @param context the context surrounding this invocation
     */
    public void inheritSlots(@NonNull LivingEntity entity, @NonNull LivingEntity other, int min, int max, @NonNull EquipContext context) {
        untrackDefault(entity);

        AccessoryData otherData = OhmegaDataAttachments.getData(other);
        int otherSize = otherData.size();

        if (max < 0) {
            entries.ensureCapacity(otherSize);
        } else {
            entries.ensureCapacity(max);
        }

        int size = size();

        for (int i = min; i < otherSize && (i <= max || max < 0); i++) {
            if (size > i) {
                entries.get(i).setType(entity, otherData.getEntry(i).getType(), context);
            } else {
                entries.add(new AccessoryDataEntry(otherData.getEntry(i).getType()));
            }
        }

        onMutateSlots(entity);
        trySendPacketToAll(entity.level(), new SyncSlotsPacket(
                SyncSlotsPacket.Action.INHERIT,
                entity.getId(),
                new int[]{other.getId(), min, max},
                Optional.empty(),
                context));
    }

    /**
     * Inserts the provided number slots of the given type at the provided index
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param index the slot index to begin inserting at
     * @param type the {@link AccessoryType} of the slots to insert
     * @param amount the number of slots to insert
     * @param context the context surrounding this invocation
     */
    public void insertSlots(@NonNull LivingEntity entity, int index, @NonNull AccessoryType type, int amount, @NonNull EquipContext context) {
        untrackDefault(entity);

        int size = size();
        boolean flag = index != size;

        entries.ensureCapacity(size + amount);

        for (int i = 0; i < amount; i++) {
            int entryIndex = index + i;

            entries.add(entryIndex, new AccessoryDataEntry(type));
        }

        if (flag) {
            resetSlotDataComponents();
        }

        onMutateSlots(entity);
        trySendPacketToAll(entity.level(), new SyncSlotsPacket(
                SyncSlotsPacket.Action.INSERT,
                entity.getId(),
                new int[]{index, amount},
                Optional.of(type),
                context));
    }

    /**
     * Adds the provided number of slots of the given type at the end of the slot list
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param type the {@link AccessoryType} of the slots to insert
     * @param amount the number of slots to insert
     * @param context the context surrounding this invocation
     * @apiNote Internally this is just a {@link #insertSlots(LivingEntity, int, AccessoryType, int, EquipContext)} call,
     * with the {@code index} parameter passed as {@link #size()}
     */
    public void addSlots(@NonNull LivingEntity entity, @NonNull AccessoryType type, int amount, @NonNull EquipContext context) {
        insertSlots(entity, size(), type, amount, context);
    }

    /**
     * Removes a give number of slots starting from the given index, matching an optionally provided filter
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param index the index at which removals should begin
     * @param amount the number of slots to remove
     * @param filter the filter for removals
     * @param context the context surrounding this invocation
     * @return the number of slots removed
     */
    public int removeSlots(@NonNull LivingEntity entity, int index, int amount, @Nullable Predicate<AccessoryType> filter, @NonNull EquipContext context) {
        untrackDefault(entity);

        int size = size();
        int count = 0;
        IntArrayList list = new IntArrayList(Math.min(amount, size - index));

        for (int i = Math.min(index + amount, size) - 1; i >= index && count < amount; i--) {
            if (filter == null || filter.test(entries.get(i).getType())) {
                count++;

                entries.remove(i).moveOrDropStack(entity, context);
                list.add(i);
            }
        }

        resetSlotDataComponents();
        onMutateSlots(entity);
        trySendPacketToAll(entity.level(), new SyncSlotsPacket(
                SyncSlotsPacket.Action.REMOVE,
                entity.getId(),
                list.toIntArray(),
                Optional.empty(),
                context));
        return count;
    }

    /**
     * Removes the provided slots corresponding to the given indexes.
     * This version should really only be used internally for synchronisation, but it is still publicly accessible nonetheless
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param indexes an array of slot indexes to remove
     * @param context the context surrounding this invocation
     * @apiNote removes slots in reverse order, and as such this assumes that the {@code indexes} parameter will be a sorted array.
     * Providing a non-sorted array may lead to unexpected behaviour and index exception throws
     */
    public void removeSlots(@NonNull LivingEntity entity, int @NonNull [] indexes, @NonNull EquipContext context) {
        untrackDefault(entity);

        for (int i = indexes.length - 1; i >= 0; i--) {
            int index = indexes[i];

            entries.remove(index).moveOrDropStack(entity, context);
        }

        resetSlotDataComponents();
        onMutateSlots(entity);
        trySendPacketToAll(entity.level(), new SyncSlotsPacket(
                SyncSlotsPacket.Action.REMOVE,
                entity.getId(),
                indexes,
                Optional.empty(),
                context));
    }

    /**
     * Sets the {@link AccessoryType} of slots from a given index to a given maximum to the provided new type
     * @param entity the {@link LivingEntity} this data instance is attached to
     * @param index the index at which setting should begin
     * @param type the new {@link AccessoryType} to set the slots' types as
     * @param max the maximum index to set the slots' types as, exclusive
     * @param context the context surrounding this invocation
     */
    public void setSlots(@NonNull LivingEntity entity, int index, @NonNull AccessoryType type, int max, @NonNull EquipContext context) {
        untrackDefault(entity);

        for (int i = index; i <= max; i++) {
            entries.get(i).setType(entity, type, context);
        }

        onMutateSlots(entity);
        trySendPacketToAll(entity.level(), new SyncSlotsPacket(
                SyncSlotsPacket.Action.SET,
                entity.getId(),
                new int[]{index, max},
                Optional.of(type),
                context));
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
     * Sets the active state of an accessory item.
     * @param entity the entity wearing (or going to equip) the accessory
     * @param index the slot index that the {@link ItemStack} is in
     * @param stack {@link ItemStack} to set active state of
     * @param value {@code true} if active, {@code false} if inactive
     */
    public void setActive(@NonNull LivingEntity entity, int index, @NonNull ItemStack stack, boolean value) {
        stack.set(OhmegaDataComponents.getActive(), value);

        if (value) {
            ItemAttributeModifiers modifiers = getEntry(index).getType().getAttributeModifiers().getActive();

            stack.set(OhmegaDataComponents.getSlotActiveModifiers(), modifiers);
            changeModifiers(entity, modifiers, true);
        } else {
            // This separate approach is needed, prevents a strange crash
            changeModifiers(entity, stack.get(OhmegaDataComponents.getSlotActiveModifiers()), false);
        }

        changeModifiers(entity, stack.get(OhmegaDataComponents.getAccessoryActiveModifiers()), value);
    }

    /**
     * Sets the active state of an accessory item.
     * Slot index is inferred by the data component automatically stored on the {@link ItemStack}
     * @param entity the entity wearing (or going to equip) the accessory
     * @param stack {@link ItemStack} to set active state of
     * @param value {@code true} if active, {@code false} if inactive
     */
    public void setActive(@NonNull LivingEntity entity, @NonNull ItemStack stack, boolean value) {
        setActive(entity, OhmegaDataComponents.getSlotIndex(stack), stack, value);
    }

    /**
     * Finds the first open index of
     * @param type {@link AccessoryType} of index to find
     * @return index of the first open index matching the type, or {@code -1} if none is found
     */
    public int getFirstOpenSlot(@NonNull AccessoryType type) {
        int size = size();

        for (int i = 0; i < size; i++) {
            AccessoryDataEntry entry = getEntry(i);

            if (entry.getType().equals(type) && entry.getStack().isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Used when trying to equip an accessory via right-clicking the held item
     * @param entity {@link LivingEntity} to equip the accessory on
     * @param stack the right-clicked held {@link ItemStack}
     * @return {@link InteractionResult#SUCCESS} if equipped successfully, else {@link InteractionResult#PASS}
     */
    public @NonNull InteractionResult tryEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null) {
            int index = getFirstOpenSlot(Accessories.getType(entity, item));

            if (index >= 0) {
                ItemStack stack0 = stack.copyWithCount(1);

                if (getEntry(index).setStack(entity, stack0, index, EquipContext.USE_HELD)) {
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
     * @param stack accessory {@link ItemStack} to test against every other accessory currently worn by the entity
     * @return {@code true} if the target accessory is compatible with every other worn accessory, {@code false} otherwise
     */

    public boolean compatibleWith(@NonNull ItemStack stack) {
        for (AccessoryDataEntry entry : getEntries()) {
            ItemStack other = entry.getStack();

            if (!other.isEmpty() && !compatibleWith(stack, other))  {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieve all of the {@link ItemStack}s in an entity's accessory inventory that match a given filter
     * @param filter A predicate filter to allow or deny elements from the returned list
     * @return every matching {@link ItemStack} in the entity's accessory inventory
     * @apiNote This operation destroys the CPU caching and annoys the GC which can build up to create a noticeable performance impact if called frequently.
     * If possible, either do not use this method or cache its result
     */
    public @NonNull List<ItemStack> getStacksFiltered(@NonNull Predicate<ItemStack> filter) {
        List<ItemStack> filteredStacks = new ArrayList<>(size());

        for (AccessoryDataEntry entry : getEntries()) {
            ItemStack stack = entry.getStack();

            if (filter.test(stack)) {
                filteredStacks.add(stack);
            }
        }

        return filteredStacks;
    }

    /**
     * Check if an entity is wearing a certain {@link Item} in an accessory slot
     * @param item the item to find
     * @return {@code true} if found, {@code false} otherwise
     */
    public boolean hasAccessory(@NonNull Item item) {
        for (AccessoryDataEntry entry : getEntries()) {
            if (entry.getStack().getItem() == item) {
                return true;
            }
        }

        return false;
    }

    /**
     * Find the first {@link ItemStack} of a certain item
     * @param item the item to find
     * @return the found matching {@link ItemStack}, or else {@link ItemStack#EMPTY}
     */
    public @NonNull ItemStack getStack(@NonNull Item item) {
        for (AccessoryDataEntry entry : getEntries()) {
            ItemStack stack = entry.getStack();

            if (stack.getItem() == item) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
