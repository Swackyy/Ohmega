package com.swacky.ohmega.common.dataattachment;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.api.SoundData;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.item.Accessory;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class AccessoryData {
    public static final Codec<AccessoryData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed)),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryData::new));

    public static final MapCodec<AccessoryData> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed)),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryData::new));

    private NonNullList<ItemStack> stacks;
    private boolean[] changed;
    private boolean[] hidden;

    private AccessoryData(NonNullList<ItemStack> stacks, boolean[] changed, boolean[] hidden) {
        this.stacks = stacks;
        this.changed = changed;
        this.hidden = hidden;
    }

    private AccessoryData(List<ItemStack> stacks, List<Boolean> changed, List<Boolean> hidden) {
        this(NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0])), Booleans.toArray(changed), Booleans.toArray(hidden));
    }

    public AccessoryData() {
        int size = AccessoryHelper.getSlotTypes().size();
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.changed = new boolean[size];
        this.hidden = new boolean[size];
    }

    public int size() {
        return stacks.size();
    }

    public boolean isItemValid(LivingEntity entity, int index, @NonNull ItemStack stack, EquipContext context) {
        if (stack.isEmpty()) {
            return true;
        }

        Item item = stack.getItem();
        Accessory accessory = AccessoryHelper.getAccessory(item);

        if (accessory != null && (AccessoryHelper.compatibleWith(entity, stack) || ItemStack.isSameItem(stack, getStackInSlot(index)))) {
            return
                    AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(index) &&
                    accessory.canEquip(entity, stack, context);
        }

        return false;
    }

    public void setChanged(int index) {
        changed[index] = true;
    }

    public ItemStack getStackInSlot(int index) {
        return stacks.get(index);
    }

    public void doUnequip(LivingEntity entity, ItemStack stack) {
        Accessory accessory = AccessoryHelper.getAccessory(stack.getItem());

        if (accessory != null) {
            accessory.onUnequip(entity, stack);
            AccessoryHelper.changeModifiers(entity, AccessoryHelper.getModifiers(stack).getPassive(), true);
            AccessoryHelper.setNoSlot(stack);
        }
    }

    private void doEquip(LivingEntity entity, ItemStack stack, int index, EquipContext context) {
        Accessory accessory = AccessoryHelper.getAccessory(stack.getItem());

        if (accessory != null) {
            AccessoryHelper.setSlot(stack, index);
            AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), true);
            AccessoryHelper.changeModifiers(entity, AccessoryHelper.getModifiers(stack).getPassive(), true);
            accessory.onEquip(entity, stack, context);

            if (context == EquipContext.RIGHT_CLICK_HELD_ITEM) {
                SoundData data = accessory.getEquipSound(stack);

                if (data != null) {
                    entity.playSound(data.sound().value(), data.volume(), data.pitch());
                }
            }
        }
    }

    private void doSetStack(int index, ItemStack stack) {
        stacks.set(index, stack);
        setChanged(index);
    }

    // todo fix: Syncing with this is bugged as it will always call Accessory#onEquip afaik
    public boolean setStack(LivingEntity entity, int index, @NonNull ItemStack stack, EquipContext context, boolean bypassValidation, boolean forceOnEquip) {
        if (bypassValidation || isItemValid(entity, index, stack, context)) {
            ItemStack current = getStackInSlot(index);

            if (!ItemStack.matches(current, stack)) {
                doUnequip(entity, current);

                if (stack.isEmpty()) {
                    AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), false);
                }

                doSetStack(index, stack);

                if (forceOnEquip || AccessoryHelper.isActive(stack)) {
                    doEquip(entity, stack, index, context);
                }
            }

            return true;
        }

        return false;
    }

    public boolean setStack(LivingEntity entity, int index, @NonNull ItemStack stack, EquipContext context, boolean bypassValidation) {
        return setStack(entity, index, stack, context, bypassValidation, true);
    }

    // Use this for most general usage
    public boolean setStack(LivingEntity entity, int index, @NonNull ItemStack stack, EquipContext context) {
        return setStack(entity, index, stack, context, false);
    }

    public ItemStack remove(LivingEntity entity, int index, int amount) {
        ItemStack stack = ContainerHelper.removeItem(stacks, index, amount);

        if (!ItemStack.isSameItemSameComponents(getStackInSlot(index), stack)) {
            doUnequip(entity, stack);
            AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), false);
            setChanged(index);
        }

        return stack;
    }

    private void removeOrDropStack(LivingEntity entity, int index) {
        ItemStack stack = getStackInSlot(index);

        if (!stack.isEmpty()) {
            doUnequip(entity, stack);

            if (!(entity instanceof Player player) || !player.addItem(stack)) {
                entity.drop(stack, false, true);
            }

            setChanged(index);
        }
    }

    public int clearMatchingItems(LivingEntity entity, Predicate<ItemStack> filter, int max) {
        int removed = 0;

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
                    doUnequip(entity, stack);
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
    public void toggleHidden(LivingEntity entity, int index) {
        if (OhmegaConfig.Server.allowHideAccessories()) {
            boolean value = !isHidden(index);

            setHidden(index, value);

            if (entity.level().isClientSide()) {
                OhmegaNetworking.C2S.send(new SetHiddenPacket(index, value));
            }
        }
    }

    private void syncAllData(ServerPlayer receiver, int entityId, int[] indexes) {
        OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(entityId, indexes, hidden));
        OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entityId, indexes, stacks, false));
    }

    public void syncAllData(ServerPlayer receiver, int entityId) {
        int size = size();
        int[] allIndexes = new int[size];

        for (int i = 0; i < size; i++) {
            allIndexes[i] = i;
        }

        syncAllData(receiver, entityId, allIndexes);
    }

    // todo: im confused
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

    public void tick(LivingEntity entity) {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStackInSlot(i);
            Accessory accessory = AccessoryHelper.getAccessory(stack.getItem());

            if (accessory != null) {
                accessory.accessoryTick(entity, stack);
            }
        }

        // Syncing
        // todo: move this to an on demand approach
        if (entity.level() instanceof ServerLevel level) {
            List<Integer> indexes = new ArrayList<>();
            List<ItemStack> stacks = new ArrayList<>();

            for (int i = 0; i < size(); i++) {
                ItemStack stack = getStackInSlot(i);
                Accessory accessory = AccessoryHelper.getAccessory(stack.getItem());

                if (changed[i] || (accessory != null && accessory.autoSync(stack))) {
                    indexes.add(i);
                    stacks.add(stack);

                    changed[i] = false;
                }
            }

            if (!indexes.isEmpty()) {
                for (ServerPlayer receiver : level.getPlayers(_ -> true)) {
                    OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entity.getId(), indexes.stream().mapToInt(Integer::intValue).toArray(), stacks, true));
                }
            }
        }
    }

    public void reload(LivingEntity entity) {
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
                removeOrDropStack(entity, i);
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
                removeOrDropStack(entity, i);
            }
        }
    }
}
