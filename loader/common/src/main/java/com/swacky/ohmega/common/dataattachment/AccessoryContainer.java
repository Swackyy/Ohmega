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
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AccessoryContainer {
    public static final Codec<AccessoryContainer> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed))
    ).apply(builder, AccessoryContainer::new));

    public static final MapCodec<AccessoryContainer> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.BOOL.listOf().fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed))
    ).apply(builder, AccessoryContainer::new));

    // Currently unused, but will be when tested further and forge supports this natively
    @SuppressWarnings("unused")
    public static final StreamCodec<RegistryFriendlyByteBuf, AccessoryContainer> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, inst -> inst.stacks,
            new StreamCodec<>() {
                @Override
                public void encode(@NonNull RegistryFriendlyByteBuf buf, boolean @NonNull [] values) {
                    buf.writeVarInt(values.length);

                    for (boolean value : values) {
                        buf.writeBoolean(value);
                    }
                }

                @Override
                public boolean @NonNull [] decode(@NonNull RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    boolean[] values = new boolean[size];

                    for (int i = 0; i < size; i++) {
                        values[i] = buf.readBoolean();
                    }

                    return values;
                }
            }, inst -> inst.changed,
            AccessoryContainer::new);

    private NonNullList<ItemStack> stacks;
    private boolean[] changed;

    private AccessoryContainer(List<ItemStack> stacks, boolean[] changed) {
        this.stacks = NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0]));
        this.changed = changed;
    }

    private AccessoryContainer(List<ItemStack> stacks, List<Boolean> changed) {
        this(stacks, Booleans.toArray(changed));
    }

    public AccessoryContainer() {
        int size = AccessoryHelper.getSlotTypes().size();
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.changed = new boolean[size];
    }

    @SuppressWarnings("deprecation")
    public boolean isItemValid(Player player, int slot, @NonNull ItemStack stack) {
        if (slot >= 0 && slot < stacks.size()) {
            Item item = stack.getItem();
            IAccessory accessory = AccessoryHelper.getBoundAccessory(item);

            if (accessory != null && (AccessoryHelper.compatibleWith(player, accessory) || ItemStack.isSameItem(stack, stacks.get(slot)))) {
                return OhmegaHooks.accessoryCanEquipEvent(player, stack, accessory.canEquip(player, stack)) && AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(slot);
            }
        }

        return false;
    }

    public int getSlots() {
        return stacks.size();
    }

    public void onContentsChanged(int index) {
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
        }
    }

    private void doSetStackInSlot(int index, ItemStack stack) {
        stacks.set(index, stack);
        onContentsChanged(index);
    }

    // Try not to use this for deserialising and syncing
    public boolean setStackInSlot(Player player, int index, @NonNull ItemStack stack, EquipContext context) {
        if (stack.isEmpty() || isItemValid(player, index, stack) && AccessoryHelper.isItemAccessoryBound(stack.getItem())) {
            ItemStack current = stacks.get(index);

            if (!ItemStack.isSameItemSameComponents(current, stack)) {
                doUnequip(player, current);
                doSetStackInSlot(index, stack);
                doEquip(player, stack, index, context);
            } else if (changed[index]) {
                changed[index] = false;
            }

            return true;
        }

        return false;
    }

    private void removeStackFromSlot(Player player, int index, boolean fromDeath) {
        ItemStack stack = stacks.get(index);

        if (!stack.isEmpty()) {
            doUnequip(player, stack);

            if (fromDeath) {
                player.drop(stack, true, false);
                doSetStackInSlot(index, ItemStack.EMPTY);
            } else {
                if (!player.addItem(stack)) {
                    player.drop(stack, true);
                }

                onContentsChanged(index);
            }
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
            ItemStack stack = stacks.get(i);
            int index = indexes[i];

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
        if (player instanceof ServerPlayer svr) {
            List<Integer> slots = new ArrayList<>();
            List<ItemStack> stacks = new ArrayList<>();

            for (int i = 0; i < this.stacks.size(); i++) {
                if (changed[i]) {
                    slots.add(i);
                    stacks.add(getStackInSlot(i));

                    changed[i] = false;
                }
            }

            if (!slots.isEmpty()) {
                AccessoryHelper.syncSlots(svr, slots.stream().mapToInt(Integer::intValue).toArray(),
                        stacks, svr.level().getPlayers(svr0 -> true));
            }
        }
    }

    public void onDeath(ServerPlayer player) {
        boolean flag = switch (OhmegaConfig.Server.keepAccessoriesBehaviour()) { // Inverse
            case ALWAYS_ON -> false;
            case ALWAYS_OFF -> true;
            case DEFAULT -> !player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        };

        if (flag) {
            for (int i = 0; i < stacks.size(); i++) {
                removeStackFromSlot(player, i, true);
            }
        }
    }

    public void reloadCfg(Player player) {
        int oldSize = this.changed.length;
        int newSize = AccessoryHelper.getSlotTypes().size();

        if (newSize > oldSize) {
            // Grow data
            ItemStack[] newStacks = new ItemStack[newSize - oldSize];
            Arrays.fill(newStacks, ItemStack.EMPTY);

            stacks = NonNullList.of(ItemStack.EMPTY, ArrayUtils.addAll(stacks.toArray(new ItemStack[0]), newStacks));
            changed = ArrayUtils.addAll(changed, new boolean[newSize - oldSize]);
        } else if (newSize < oldSize) {
            // Drop stacks outside of range
            for (int i = newSize; i < oldSize; i++) {
                removeStackFromSlot(player, i, false);
            }

            // Shrink data
            stacks = NonNullList.of(ItemStack.EMPTY, Arrays.copyOfRange(this.stacks.toArray(new ItemStack[0]), 0, newSize));
            changed = Arrays.copyOfRange(this.changed, 0, newSize);
        }

        // Drop invalid stacks (mismatched accessory types and non-accessory items)
        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

        for (int i = 0; i < stacks.size(); i++) {
            if (slotTypes.get(i) != AccessoryHelper.getType(stacks.get(i).getItem())) {
                removeStackFromSlot(player, i, false);
            }
        }
    }
}
