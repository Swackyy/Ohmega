package com.swacky.ohmega.common.dataattachment;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class AccessoryContainer {
    public static final Codec<AccessoryContainer> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed)),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryContainer::new));

    public static final MapCodec<AccessoryContainer> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed)),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryContainer::new));

    private NonNullList<ItemStack> stacks;
    private boolean[] changed;
    private boolean[] hidden;

    private AccessoryContainer(NonNullList<ItemStack> stacks, boolean[] changed, boolean[] hidden) {
        this.stacks = stacks;
        this.changed = changed;
        this.hidden = hidden;
    }

    private AccessoryContainer(List<ItemStack> stacks, List<Boolean> changed, List<Boolean> hidden) {
        this(
                NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0])),
                Booleans.toArray(changed),
                Booleans.toArray(hidden));
    }

    public AccessoryContainer() {
        int size = AccessoryHelper.getSlotTypes().size();
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.changed = new boolean[size];
        this.hidden = new boolean[size];
    }

    public int size() {
        return stacks.size();
    }

    public boolean isItemValid(Player player, int index, @NonNull ItemStack stack, EquipContext context) {
        if (stack.isEmpty()) {
            return true;
        }

        if (index >= 0 && index < size()) {
            Item item = stack.getItem();
            IAccessory accessory = AccessoryHelper.getAccessory(item);

            if (accessory != null && (AccessoryHelper.compatibleWith(player, stack) || ItemStack.isSameItem(stack, getStackInSlot(index)))) {
                return OhmegaHooks.accessoryCanEquipEvent(player, stack, context, accessory.canEquip(player, stack)) && AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(index);
            }
        }

        return false;
    }

    public void setChanged(int index) {
        changed[index] = true;
    }

    public ItemStack getStackInSlot(int index) {
        return stacks.get(index);
    }

    public void doUnequip(Player player, ItemStack stack) {
        IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());

        if (accessory != null) {
            if (!OhmegaHooks.accessoryUnequipEvent(player, stack)) {
                accessory.onUnequip(player, stack);
            }

            AccessoryHelper.changeModifiers(player, AccessoryHelper.getModifiers(stack).getPassive(), true);
            AccessoryHelper.setNoSlot(stack);
        }
    }

    private void doEquip(Player player, ItemStack stack, int index, EquipContext context) {
        IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());

        if (accessory != null) {
            AccessoryHelper.setSlot(stack, index);
            AccessoryHelper.changeModifiers(player, AccessoryHelper.getModifiers(stack).getPassive(), true);

            if (!OhmegaHooks.accessoryEquipEvent(player, stack, context)) {
                accessory.onEquip(player, stack);
            }

            if (context == EquipContext.RIGHT_CLICK_HELD_ITEM) {
                Holder<SoundEvent> equipSound = accessory.getEquipSound();

                if (equipSound != null) {
                    player.playSound(equipSound.value(), 1, 1);
                }
            }
        }
    }

    private void doSetStack(int index, ItemStack stack) {
        stacks.set(index, stack);
        setChanged(index);
    }

    // todo fix: Syncing with this is bugged as it will always call IAccessory#onEquip afaik
    public boolean setStack(Player player, int index, @NonNull ItemStack stack, EquipContext context, boolean bypassValidation, boolean forceOnEquip) {
        if (bypassValidation || isItemValid(player, index, stack, context)) {
            ItemStack current = getStackInSlot(index);

            if (!ItemStack.matches(current, stack)) {
                doUnequip(player, current);
                doSetStack(index, stack);

                if (forceOnEquip || AccessoryHelper.isActive(stack)) {
                    doEquip(player, stack, index, context);
                }
            }

            return true;
        }

        return false;
    }

    public boolean setStack(Player player, int index, @NonNull ItemStack stack, EquipContext context, boolean bypassValidation) {
        return setStack(player, index, stack, context, bypassValidation, true);
    }

    // Use this for most general usage
    public boolean setStack(Player player, int index, @NonNull ItemStack stack, EquipContext context) {
        return setStack(player, index, stack, context, false);
    }

    public ItemStack remove(Player player, int index, int amount) {
        ItemStack stack = ContainerHelper.removeItem(stacks, index, amount);

        if (!ItemStack.isSameItemSameComponents(getStackInSlot(index), stack)) {
            doUnequip(player, stack);
            setChanged(index);
        }

        return stack;
    }

    private void removeOrDropStack(Player player, int index) {
        ItemStack stack = getStackInSlot(index);

        if (!stack.isEmpty()) {
            doUnequip(player, stack);

            if (!player.addItem(stack)) {
                player.drop(stack, true);
            }

            setChanged(index);
        }
    }

    public int clearMatchingItems(Player player, Predicate<ItemStack> filter, int max) {
        int removed = 0;

        for (int i = 0; i < size() && (max < 0 || removed < max); i++) {
            ItemStack stack = getStackInSlot(i);

            if (filter.test(stack)) {
                int count = stack.count();
                int toRemoveCurrentStack;

                if (max < 0) {
                    toRemoveCurrentStack = count;
                } else {
                    toRemoveCurrentStack = Math.min(count, Math.max(0, max - removed));
                }

                if (max < 0 || removed + count <= max) {
                    doUnequip(player, stack);
                }

                stack.shrink(toRemoveCurrentStack);
                setChanged(i);

                removed += toRemoveCurrentStack;
            }
        }

        return removed;
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    public boolean isHidden(int index) {
        return hidden[index];
    }

    // Used in sync
    public void setHidden(int index, boolean value) {
        hidden[index] = value;
    }

    // Use in client toggling
    public void toggleHidden(Player player, int index) {
        if (OhmegaConfig.Server.allowHideAccessories()) {
            boolean value = !isHidden(index);

            setHidden(index, value);

            if (player.level().isClientSide()) {
                OhmegaNetworking.C2S.send(new SetHiddenPacket(index, value));
            }
        }
    }

    private void syncAllData(ServerPlayer receiver, int senderId, int[] allIndexes) {
        OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(senderId, allIndexes, hidden));
        OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(senderId, allIndexes, stacks, false));
    }

    public void syncAllData(ServerPlayer receiver, int playerId) {
        int size = size();
        int[] allIndexes = new int[size];

        for (int i = 0; i < size; i++) {
            allIndexes[i] = i;
        }

        syncAllData(receiver, playerId, allIndexes);
    }

    public void onAttach(ServerPlayer player) {
        // If the server config gets de-synced, this fixes it instead of throwing
        reload(player);

        int size = size();
        int[] allIndexes = new int[size];

        for (int i = 0; i < size; i++) {
            ItemStack stack = getStackInSlot(i);

            if (AccessoryHelper.isActive(stack)) {
                doEquip(player, stack, i, EquipContext.GENERIC);
            }

            allIndexes[i] = i;
        }

        // Initial load syncing
        syncAllData(player, player.getId(), allIndexes);
    }

    public void tick(Player player) {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStackInSlot(i);
            IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());

            if (accessory != null && !OhmegaHooks.accessoryTickEventPre(player, stack)) {
                accessory.accessoryTick(player, stack);
                OhmegaHooks.accessoryTickEventPost(player, stack);
            }
        }

        // Syncing
        // todo: move this to an on demand approach and add initial sync to onAttach
        if (player instanceof ServerPlayer svr) {
            List<Integer> indexes = new ArrayList<>();
            List<ItemStack> stacks = new ArrayList<>();

            for (int i = 0; i < size(); i++) {
                ItemStack stack = getStackInSlot(i);
                IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());

                if (changed[i] || (accessory != null && accessory.autoSync(player, stack))) {
                    indexes.add(i);
                    stacks.add(stack);

                    changed[i] = false;
                }
            }

            if (!indexes.isEmpty()) {
                for (ServerPlayer receiver : svr.level().getPlayers(_ -> true)) {
                    OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(svr.getId(), indexes.stream().mapToInt(Integer::intValue).toArray(), stacks, true));
                }
            }
        }
    }

    public void reload(Player player) {
        int oldSize = Math.min(changed.length, size());
        int newSize = AccessoryHelper.getSlotTypes().size();

        if (newSize > oldSize) {
            // Grow data
            ItemStack[] emptyStackArray = new ItemStack[newSize - oldSize];
            Arrays.fill(emptyStackArray, ItemStack.EMPTY);

            stacks = NonNullList.of(ItemStack.EMPTY, ArrayUtils.addAll(stacks.toArray(new ItemStack[0]), emptyStackArray));
            changed = ArrayUtils.addAll(changed, new boolean[newSize - oldSize]);
            hidden = ArrayUtils.addAll(hidden, new boolean[newSize - oldSize]);
        } else if (newSize < oldSize) {
            // Drop stacks outside of range
            for (int i = newSize; i < oldSize; i++) {
                removeOrDropStack(player, i);
            }

            // Shrink data
            stacks = NonNullList.of(ItemStack.EMPTY, Arrays.copyOfRange(stacks.toArray(new ItemStack[0]), 0, newSize));
            changed = Arrays.copyOfRange(changed, 0, newSize);
            hidden = Arrays.copyOfRange(hidden, 0, newSize);
        }

        // Drop invalid stacks (mismatched accessory types and non-accessory items)
        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

        for (int i = 0; i < size(); i++) {
            if (slotTypes.get(i) != AccessoryHelper.getType(getStackInSlot(i).getItem())) {
                removeOrDropStack(player, i);
            }
        }
    }
}
