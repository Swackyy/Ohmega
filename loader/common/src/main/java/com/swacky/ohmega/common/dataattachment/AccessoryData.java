package com.swacky.ohmega.common.dataattachment;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.item.Accessory;
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

public final class AccessoryData {
    public static final Codec<AccessoryData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryData::new));

    public static final MapCodec<AccessoryData> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("hidden").forGetter(inst -> Booleans.asList(inst.hidden))
    ).apply(builder, AccessoryData::new));

    private NonNullList<ItemStack> stacks;
    private boolean[] hidden;
    private long tickIndex = 0;

    private AccessoryData(NonNullList<ItemStack> stacks, boolean[] hidden) {
        this.stacks = stacks;
        this.hidden = hidden;
    }

    private AccessoryData(List<ItemStack> stacks, List<Boolean> hidden) {
        this(NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0])), Booleans.toArray(hidden));
    }

    public AccessoryData() {
        int size = AccessoryHelper.getSlotTypes().size();
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
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
        Accessory accessory = Accessories.get(item);

        if (accessory != null && (AccessoryHelper.compatibleWith(entity, stack) || ItemStack.isSameItem(stack, getStackInSlot(index)))) {
            return
                    AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(index) &&
                    accessory.canEquip(entity, stack, context);
        }

        return false;
    }

    public ItemStack getStackInSlot(int index) {
        return stacks.get(index);
    }

    public void doUnequip(LivingEntity entity, ItemStack stack) {
        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null) {
            accessory.onUnequip(entity, stack);
            AccessoryHelper.changeModifiers(entity, stack.get(DataComponents.ATTRIBUTE_MODIFIERS), true);
            AccessoryHelper.setNoSlot(stack);
        }
    }

    private void doEquip(LivingEntity entity, ItemStack stack, int index, EquipContext context) {
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

    private void syncWithPlayers(LivingEntity entity, int[] indexes, List<ItemStack> stacks) {
        if (entity.level() instanceof ServerLevel level) {
            for (ServerPlayer receiver : level.players()) {
                OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entity.getId(), indexes, stacks, true));
            }
        }
    }

    private void doSetStack(LivingEntity entity, int index, ItemStack stack, EquipContext context, boolean forceOnEquip, boolean sync) {
        ItemStack current = getStackInSlot(index);

        if (!ItemStack.matches(current, stack)) {
            doUnequip(entity, current);

            if (stack.isEmpty()) {
                AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), false);
            }

            stacks.set(index, stack);

            if (forceOnEquip || AccessoryHelper.isActive(stack)) {
                doEquip(entity, stack, index, context);
            }

            if (sync) {
                syncWithPlayers(entity, new int[]{index}, List.of(stack));
            }
        }
    }

    // todo fix: Syncing with this is bugged as it will always call Accessory#onEquip afaik
    public boolean setStack(LivingEntity entity, int index, ItemStack stack, EquipContext context, boolean bypassValidation, boolean forceOnEquip) {
        if (bypassValidation || isItemValid(entity, index, stack, context)) {
            doSetStack(entity, index, stack, context, forceOnEquip, true);
            return true;
        }

        return false;
    }

    // Use this for most general usage
    public boolean setStack(LivingEntity entity, int index, @NonNull ItemStack stack, EquipContext context) {
        return setStack(entity, index, stack, context, false, true);
    }

    public void setStacks(LivingEntity entity, int[] indexes, List<ItemStack> stacks, EquipContext context, boolean forceOnEquip) {
        for (int i = 0; i < indexes.length; i++) {
            doSetStack(entity, indexes[i], stacks.get(i), context, forceOnEquip, false);
        }

        syncWithPlayers(entity, indexes, stacks);
    }

    public void setStacksRange(LivingEntity entity, int minIndex, int maxIndex, List<ItemStack> allStacks, EquipContext context, boolean forceOnEquip) {
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

    public ItemStack remove(LivingEntity entity, int index, int amount) {
        ItemStack stack;

        if (amount < 0) {
            stack = ContainerHelper.takeItem(stacks, index);
        } else {
            stack = ContainerHelper.removeItem(stacks, index, amount);
        }

        if (!ItemStack.isSameItemSameComponents(getStackInSlot(index), stack)) {
            doUnequip(entity, stack);
            AccessoryHelper.changeModifiers(entity, AccessoryHelper.getSlotTypes().get(index).getAttributeModifiers().getPassive(), false);
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
        }
    }

    public int clearMatchingItems(LivingEntity entity, Predicate<ItemStack> filter, int max) {
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
                    doUnequip(entity, stack);
                    indexes.add(i);
                }

                stack.shrink(toRemoveCurrentStack);

                removed += toRemoveCurrentStack;
            }
        }

        syncWithPlayers(entity, indexes.toIntArray(), NonNullList.withSize(indexes.size(), ItemStack.EMPTY));
        return removed;
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    public boolean[] getHidden() {
        return hidden;
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
        if (OhmegaConfig.Server.getData().allowHideAccessories().get()) {
            boolean value = !isHidden(index);

            setHidden(index, value);

            if (entity.level().isClientSide()) {
                OhmegaNetworking.C2S.send(new SetHiddenPacket(index, value));
            }
        }
    }

    private void syncAllData(ServerPlayer receiver, int entityId, int[] indexes) {
        OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(entityId, indexes, hidden));
        OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entityId, indexes, stacks, true));
    }

    public void syncAllData(ServerPlayer receiver, int entityId) {
        int size = size();
        int[] allIndexes = new int[size];

        for (int i = 0; i < size; i++) {
            allIndexes[i] = i;
        }

        syncAllData(receiver, entityId, allIndexes);
    }

    // todo: call on neoforge player respawn
    public void onAttach(ServerPlayer player) {
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
        menu.sendAllDataToRemote();
    }

    public void tick(LivingEntity entity) {
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

    public void reload(LivingEntity entity) {
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
                removeOrDropStack(entity, i);
            }

            // Shrink data
            stacks = NonNullList.of(ItemStack.EMPTY, Arrays.copyOfRange(stacks.toArray(new ItemStack[0]), 0, newSize));
            hidden = Arrays.copyOfRange(hidden, 0, newSize);
        }

        // Drop invalid stacks (mismatched accessory types and non-accessory items)
        for (int i = 0; i < size(); i++) {
            if (types.get(i) != AccessoryHelper.getType(getStackInSlot(i).getItem())) {
                removeOrDropStack(entity, i);
            }
        }
    }
}
