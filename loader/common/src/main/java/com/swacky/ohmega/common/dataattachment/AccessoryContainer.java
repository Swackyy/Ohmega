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
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.common.SetVisibilityPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
            Codec.BOOL.listOf().fieldOf("visibility").forGetter(inst -> Booleans.asList(inst.visibility))
    ).apply(builder, AccessoryContainer::new));

    // todo: check if can be removed
    public static final MapCodec<AccessoryContainer> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed)),
            Codec.BOOL.listOf().fieldOf("visibility").forGetter(inst -> Booleans.asList(inst.visibility))
    ).apply(builder, AccessoryContainer::new));

    private NonNullList<ItemStack> stacks;
    private boolean[] changed;
    private boolean[] visibility;
    private boolean forceOnEquip = false;

    private AccessoryContainer(List<ItemStack> stacks, boolean[] changed, boolean[] visibility) {
        this.stacks = NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0]));
        this.changed = changed;
        this.visibility = visibility;
    }

    private AccessoryContainer(List<ItemStack> stacks, List<Boolean> changed, List<Boolean> visibility) {
        this(stacks, Booleans.toArray(changed), Booleans.toArray(visibility));
    }

    public AccessoryContainer() {
        int size = AccessoryHelper.getSlotTypes().size();
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.changed = new boolean[size];
        this.visibility = new boolean[size];
        Arrays.fill(visibility, true);
    }

    public boolean isItemValid(Player player, int index, @NonNull ItemStack stack, EquipContext context) {
        if (stack.isEmpty()) {
            return true;
        }

        if (index >= 0 && index < stacks.size()) {
            Item item = stack.getItem();
            IAccessory accessory = AccessoryHelper.getBoundAccessory(item);

            if (accessory != null && (AccessoryHelper.compatibleWith(player, stack) || ItemStack.isSameItem(stack, stacks.get(index)))) {
                return OhmegaHooks.accessoryCanEquipEvent(player, stack, context, accessory.canEquip(player, stack)) && AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(index);
            }
        }

        return false;
    }

    public int getSize() {
        return stacks.size();
    }

    public void setChanged(int index) {
        changed[index] = true;
    }

    public ItemStack getStackInSlot(int index) {
        return stacks.get(index);
    }

    public void doUnequip(Player player, ItemStack stack) {
        IAccessory accessory = AccessoryHelper.getBoundAccessory(stack.getItem());

        if (accessory != null) {
            if (!OhmegaHooks.accessoryUnequipEvent(player, stack)) {
                accessory.onUnequip(player, stack);
            }

            AccessoryHelper.changeModifiers(player, AccessoryHelper.getModifiers(stack).getPassive(), true);
            AccessoryHelper.setNoSlot(stack);
        }
    }

    private void doEquip(Player player, ItemStack stack, int index, EquipContext context) {
        IAccessory accessory = AccessoryHelper.getBoundAccessory(stack.getItem());

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

    private void doSetStackInSlot(int index, ItemStack stack) {
        stacks.set(index, stack);
        setChanged(index);
    }

    public boolean setStackInSlot(Player player, int index, @NonNull ItemStack stack, EquipContext context, boolean bypassValidation, boolean forceOnEquip) {
        if (bypassValidation || isItemValid(player, index, stack, context)) {
            ItemStack current = stacks.get(index);

            if (!ItemStack.matches(current, stack)) {
                doUnequip(player, current);
                doSetStackInSlot(index, stack);

                if (forceOnEquip || AccessoryHelper.isActive(stack)) {
                    doEquip(player, stack, index, context);
                }
            } else if (changed[index]) {
                changed[index] = false;
            }

            return true;
        }

        return false;
    }

    public boolean setStackInSlot(Player player, int index, @NonNull ItemStack stack, EquipContext context, boolean bypassValidation) {
        return setStackInSlot(player, index, stack, context, bypassValidation, true);
    }

    // Use this for most general usage
    public boolean setStackInSlot(Player player, int index, @NonNull ItemStack stack, EquipContext context) {
        return setStackInSlot(player, index, stack, context, false);
    }

    private void removeStackFromSlot(Player player, int index) {
        ItemStack stack = stacks.get(index);

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

        for (int i = 0; i < stacks.size() && (max < 0 || removed < max); i++) {
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

    public boolean isVisible(int index) {
        return visibility[index];
    }

    public void setVisibility(Player player, int index, boolean value) {
        visibility[index] = value;
        SetVisibilityPacket packet = new SetVisibilityPacket(index, value);

        if (player.level().isClientSide()) {
            // Sync with server
            OhmegaNetworking.C2S.send(packet);
        } else for (ServerPlayer receiver : ((ServerPlayer) player).level().getPlayers(_ -> true)) {
            // Sync with clients
            OhmegaNetworking.S2C.send(receiver, packet);
        }
    }

    public void onAttach(Player player) {
        // If the server config gets de-synced, this fixes it instead of throwing
        reloadCfg(player);

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);

            doSetStackInSlot(i, stack);

            if (AccessoryHelper.isActive(stack)) {
                doEquip(player, stack, i, EquipContext.GENERIC);
            }
        }
    }

    public void syncSlots(Player player, int[] indexes, List<ItemStack> stacks) {
        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            ItemStack current = getStackInSlot(index);
            ItemStack stack = stacks.get(i);

            if (!ItemStack.isSameItemSameComponents(current, stack)) {
                doUnequip(player, current);
            }

            doSetStackInSlot(index, stack);

            if (AccessoryHelper.isActive(stack)) {
                doEquip(player, stack, index, EquipContext.GENERIC);
            }
        }
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    public void tick(Player player) {
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = getStackInSlot(i);
            IAccessory accessory = AccessoryHelper.getBoundAccessory(stack.getItem());

            if (accessory != null && !OhmegaHooks.accessoryTickEventPre(player, stack)) {
                accessory.tick(player, stack);
                OhmegaHooks.accessoryTickEventPost(player, stack);
            }
        }

        // Syncing
        // todo: see if it is a good idea to split this into just autoSync in tick and on demand for changing
        if (player instanceof ServerPlayer svr) {
            List<Integer> slots = new ArrayList<>();
            List<ItemStack> stacks = new ArrayList<>();

            for (int i = 0; i < this.stacks.size(); i++) {
                ItemStack stack = getStackInSlot(i);
                IAccessory accessory = AccessoryHelper.getBoundAccessory(stack.getItem());

                if (changed[i] || (accessory != null && accessory.autoSync(player, stack))) {
                    slots.add(i);
                    stacks.add(stack);

                    changed[i] = false;
                }
            }

            if (!slots.isEmpty()) {
                for (ServerPlayer receiver : svr.level().getPlayers(_ -> true)) {
                    OhmegaNetworking.S2C.send(receiver, new SyncAccessorySlotsPacket(receiver.getId(), slots.stream().mapToInt(Integer::intValue).toArray(), stacks, forceOnEquip));
                }

                forceOnEquip = true;
            }
        }
    }

    public void reloadCfg(Player player) {
        int oldSize = Math.min(changed.length, stacks.size());
        int newSize = AccessoryHelper.getSlotTypes().size();

        if (newSize > oldSize) {
            // Grow data
            ItemStack[] newStacks = new ItemStack[newSize];
            Arrays.fill(newStacks, ItemStack.EMPTY);

            stacks = NonNullList.of(ItemStack.EMPTY, ArrayUtils.addAll(stacks.toArray(new ItemStack[0]), newStacks));
            changed = ArrayUtils.addAll(changed, new boolean[newSize]);
            visibility = ArrayUtils.addAll(visibility, new boolean[newSize]);
        } else if (newSize < oldSize) {
            // Drop stacks outside of range
            for (int i = newSize; i < oldSize; i++) {
                removeStackFromSlot(player, i);
            }

            // Shrink data
            stacks = NonNullList.of(ItemStack.EMPTY, Arrays.copyOfRange(stacks.toArray(new ItemStack[0]), 0, newSize));
            changed = Arrays.copyOfRange(changed, 0, newSize);
            visibility = Arrays.copyOfRange(visibility, 0, newSize);
        }

        // Drop invalid stacks (mismatched accessory types and non-accessory items)
        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

        for (int i = 0; i < stacks.size(); i++) {
            if (slotTypes.get(i) != AccessoryHelper.getType(stacks.get(i).getItem())) {
                removeStackFromSlot(player, i);
            }
        }
    }
}
