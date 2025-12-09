package com.swacky.ohmega.common.dataattachment;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.ModifierHolder;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessoryInvDataAttachment {
    private static final MapCodec<AccessoryInvDataAttachment> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("stacks").forGetter(inst -> inst.stacks),
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("previous").forGetter(inst -> inst.previous),
            Codec.list(Codec.BOOL).fieldOf("changed").forGetter(inst -> Booleans.asList(inst.changed))
    ).apply(builder, AccessoryInvDataAttachment::new));

    public static final IAttachmentSerializer<AccessoryInvDataAttachment> SERIALIZER = new IAttachmentSerializer<>() {
        @SuppressWarnings("deprecation")
        @Override
        public @NotNull AccessoryInvDataAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            AccessoryInvDataAttachment data = input.read(CODEC).orElseThrow();
            data.initialise((Player) holder);
            return data;
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean write(@NotNull AccessoryInvDataAttachment data, ValueOutput output) {
            output.store(CODEC, data);
            return true;
        }
    };

    private NonNullList<ItemStack> stacks;
    private NonNullList<ItemStack> previous;
    private boolean[] changed;

    private AccessoryInvDataAttachment(List<ItemStack> stacks, List<ItemStack> previous, boolean[] changed) {
        this.stacks = NonNullList.of(ItemStack.EMPTY, stacks.toArray(new ItemStack[0]));
        this.previous = NonNullList.of(ItemStack.EMPTY, previous.toArray(new ItemStack[0]));
        this.changed = changed;
    }

    private AccessoryInvDataAttachment(List<ItemStack> stacks, List<ItemStack> previous, List<Boolean> changed) {
        this(stacks, previous, Booleans.toArray(changed));
    }

    public AccessoryInvDataAttachment() {
        int size = AccessoryHelper.getSlotTypes().size();
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.previous = NonNullList.withSize(size, ItemStack.EMPTY);
        this.changed = new boolean[size];
    }

    public void initialise(Player player) {
        for (int i = 0; i < this.getSlots(); i++) {
            ItemStack stack = this.getStackInSlot(i);

            if (!stack.isEmpty()) {
                ModifierHolder modifiers = AccessoryHelper.getModifiers(stack);
                AccessoryHelper.changeModifiers(player, modifiers.getPassive(), true);
                if (AccessoryHelper.isActive(stack)) {
                    AccessoryHelper.changeModifiers(player, modifiers.getActive(), true);
                }
            }
        }
    }

    public void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    public int getSlots() {
        return this.stacks.size();
    }

    public void onContentsChanged(int index) {
        this.changed[index] = true;
    }

    public ItemStack getStackInSlot(int index) {
        this.validateSlotIndex(index);
        return this.stacks.get(index);
    }

    public void setStackInSlot(int index, @NotNull ItemStack stack) {
        this.validateSlotIndex(index);
        this.previous.set(index, this.getStackInSlot(index));
        this.stacks.set(index, stack);
        this.onContentsChanged(index);
    }

    public ItemStack removeItem(int index, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.stacks, index, amount);
        if (!stack.isEmpty()) {
            this.onContentsChanged(index);
        }

        return stack;
    }

    public void sync(Player player) {
        if (player instanceof ServerPlayer svr) {
            List<ServerPlayer> receivers = new ArrayList<>(svr.level().getPlayers((svr0) -> true));
            receivers.add(svr);

            List<Integer> slots = new ArrayList<>();
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < getSlots(); i++) {
                ItemStack stack = getStackInSlot(i);
                boolean autoSync;
                IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                if (acc != null) {
                    autoSync = acc.autoSync(svr);
                } else {
                    autoSync = false;
                }
                if (autoSync && !ItemStack.isSameItemSameComponents(stack, this.previous.get(i)) || this.changed[i]) {
                    slots.add(i);
                    stacks.add(stack);
                    this.changed[i] = false;
                    this.previous.set(i, stack.copy());
                }
            }
            if (!slots.isEmpty()) {
                AccessoryHelper.syncSlots(svr, slots.stream().mapToInt(Integer::intValue).toArray(), stacks, receivers);
            }
        }
    }

    public void invalidate(Player player) {
        boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
            case ON -> false;
            case OFF -> true;
            case DEFAULT -> {
                MinecraftServer server = player.level().getServer();
                yield server == null || !server.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            }
        };

        if (flag) {
            for (int i = 0; i < this.getSlots(); i++) {
                ItemStack stack = this.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    this.setStackInSlot(i, ItemStack.EMPTY);
                    IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                    if (acc != null) {
                        if (!OhmegaHooks.accessoryUnequipEvent(player, stack)) {
                            acc.onUnequip(player, stack);
                        }
                        AccessoryHelper.setSlot(stack, -1);
                        player.drop(stack, true, false);
                    }
                }
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
            this.stacks = NonNullList.of(ItemStack.EMPTY, ArrayUtils.addAll(this.stacks.toArray(new ItemStack[0]), newStacks));
            this.changed = ArrayUtils.addAll(this.changed, new boolean[newSize - oldSize]);
            ItemStack[] newPrevious = new ItemStack[newSize - oldSize];
            Arrays.fill(newPrevious, ItemStack.EMPTY);
            this.previous = NonNullList.of(ItemStack.EMPTY, ArrayUtils.addAll(this.previous.toArray(new ItemStack[0]), newPrevious));
        } else if (newSize < oldSize) {
            // Drop stacks outside of range
            for (ItemStack stack : Arrays.copyOfRange(this.stacks.toArray(new ItemStack[0]), newSize, oldSize)) {
                if (!stack.isEmpty()) {
                    IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                    if (acc != null) {
                        if (!OhmegaHooks.accessoryUnequipEvent(player, stack)) {
                            acc.onUnequip(player, stack);
                        }
                        AccessoryHelper.setSlot(stack, -1);
                    }

                    if (!player.addItem(stack)) {
                        player.drop(stack, false);
                    }
                }
            }

            // Shrink data
            this.stacks = NonNullList.of(ItemStack.EMPTY, Arrays.copyOfRange(this.stacks.toArray(new ItemStack[0]), 0, newSize));
            this.changed = Arrays.copyOfRange(this.changed, 0, newSize);
            this.previous = NonNullList.of(ItemStack.EMPTY, Arrays.copyOfRange(this.previous.toArray(new ItemStack[0]), 0, newSize));
        }

        // Drop invalid stacks (mismatched accessory types and non-accessory items)
        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                IAccessory acc = AccessoryHelper.getBoundAccessory(item);

                if (slotTypes.get(i) != AccessoryHelper.getType(item)) {
                    if (acc != null) {
                        if (!OhmegaHooks.accessoryUnequipEvent(player, stack)) {
                            acc.onUnequip(player, stack);
                        }
                        AccessoryHelper.setSlot(stack, -1);
                    }

                    if (!player.addItem(stack)) {
                        player.drop(stack, false);
                    }
                }
            }
        }
    }
}
