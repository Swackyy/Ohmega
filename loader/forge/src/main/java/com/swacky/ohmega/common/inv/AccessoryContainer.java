package com.swacky.ohmega.common.inv;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.ModifierHolder;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessoryContainer extends ItemStackHandler {
    private final Player player;
    private NonNullList<ItemStack> previous;
    private boolean[] changed;

    public AccessoryContainer(Player player) {
        super(AccessoryHelper.getSlotTypes().size());
        this.player = player;

        int size = AccessoryHelper.getSlotTypes().size();
        this.changed = new boolean[size];
        this.previous = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot >= 0 && slot < this.getSlots()) {
            Item item = stack.getItem();
            IAccessory acc = AccessoryHelper.getBoundAccessory(item);
            if (acc != null && (AccessoryHelper.compatibleWith(this.player, acc) || ItemStack.isSameItem(stack, this.getStackInSlot(slot)))) {
                return OhmegaHooks.accessoryCanEquipEvent(this.player, stack, acc.canEquip(this.player, stack)).getReturnValue() && AccessoryHelper.getType(item) == AccessoryHelper.getSlotTypes().get(slot);
            }
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookup) {
        CompoundTag tag = super.serializeNBT(lookup);

        ListTag previousList = new ListTag();
        for (int i = 0; i < this.previous.size(); i++) {
            ItemStack stack = this.previous.get(i);
            if (!stack.isEmpty()) {
                CompoundTag element = new CompoundTag();
                element.putByte("Slot", (byte) i);
                previousList.add(stack.save(lookup, element));
            }
        }
        tag.put("PreviousItems", previousList);

        ListTag changedList = new ListTag();
        for (boolean bool : this.changed) {
            CompoundTag element = new CompoundTag();
            element.putBoolean("Value", bool);
            changedList.add(element);
        }
        tag.put("Changed", changedList);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag tag) {
        onLoad();

        ListTag stacksList = tag.getList("Items").orElseThrow();
        for (int i = 0; i < stacksList.size(); i++) {
            CompoundTag element = stacksList.getCompound(i).orElseThrow();
            int j = element.getByte("Slot").orElseThrow() & 255;
            if (j < this.stacks.size()) {
                ItemStack stack = ItemStack.parse(lookup, element).orElse(ItemStack.EMPTY);
                if (isItemValid(j, stack)) {
                    this.stacks.set(j, stack);
                } else if (this.player instanceof ServerPlayer) {
                    if (!this.player.addItem(stack)) {
                        this.player.drop(stack, false);
                    }
                }
            }
        }


        ListTag previousList = tag.getList("PreviousItems").orElseThrow();
        for (int i = 0; i < previousList.size(); i++) {
            CompoundTag element = previousList.getCompound(i).orElseThrow();
            int j = element.getByte("Slot").orElseThrow() & 255;
            if (j < this.previous.size()) {
                this.previous.set(j, ItemStack.parse(lookup, element).orElse(ItemStack.EMPTY));
            }
        }

        ListTag changedList = tag.getList("Changed").orElseThrow();
        for (int i = 0; i < Math.min(changedList.size(), this.changed.length); i++) {
            this.changed[i] = changedList.getCompound(i).orElseThrow().getBoolean("Value").orElseThrow();
        }

        for (int i = 0; i < this.getSlots(); i++) {
            ItemStack stack = this.getStackInSlot(i);

            ModifierHolder modifiers = AccessoryHelper.getModifiers(stack);
            AccessoryHelper.changeModifiers(this.player, modifiers.getPassive(), true);
            if (AccessoryHelper.isActive(stack)) {
                AccessoryHelper.changeModifiers(this.player, modifiers.getActive(), true);
            }
        }
    }

    @Override
    public void onContentsChanged(int slot) {
        this.changed[slot] = true;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        trySetStackInSlot(slot, stack);
    }

    public boolean trySetStackInSlot(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty() || this.isItemValid(slot, stack) && AccessoryHelper.isItemAccessoryBound(stack.getItem())) {
            this.previous.set(slot, this.getStackInSlot(slot).copy());
            super.setStackInSlot(slot, stack);
            return true;
        }
        return false;
    }

    public void tick() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
            if (acc != null && !OhmegaHooks.accessoryTickEventPre(this.player, stack).isCanceled()) {
                acc.tick(this.player, stack);
                OhmegaHooks.accessoryTickEventPost(this.player, stack);
            }
        }
        this.sync();
    }

    private void sync() {
        if (this.player instanceof ServerPlayer svr) {
            List<ServerPlayer> receivers = new ArrayList<>(((ServerLevel) svr.level()).getPlayers((svr0) -> true));
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

    public void invalidate() {
        boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
            case ON -> false;
            case OFF -> true;
            case DEFAULT -> this.player.getServer() == null || !this.player.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        };

        if (flag) {
            for (int i = 0; i < this.getSlots(); i++) {
                ItemStack stack = this.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    this.setStackInSlot(i, ItemStack.EMPTY);
                    IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                    if (acc != null) {
                        if (!OhmegaHooks.accessoryUnequipEvent(this.player, stack).isCanceled()) {
                            acc.onUnequip(this.player, stack);
                        }
                        AccessoryHelper.setSlot(stack, -1);
                        this.player.drop(stack, true, false);
                    }
                }
            }
        }
    }

    /**
     * Internal use only, do not use
     */
    public void reloadCfg() {
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
                        if (!OhmegaHooks.accessoryUnequipEvent(this.player, stack).isCanceled()) {
                            acc.onUnequip(this.player, stack);
                        }
                        AccessoryHelper.setSlot(stack, -1);
                    }

                    if (!this.player.addItem(stack)) {
                        this.player.drop(stack, false);
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
                        if (!OhmegaHooks.accessoryUnequipEvent(this.player, stack).isCanceled()) {
                            acc.onUnequip(this.player, stack);
                        }
                        AccessoryHelper.setSlot(stack, -1);
                    }

                    if (!this.player.addItem(stack)) {
                        this.player.drop(stack, false);
                    }
                }
            }
        }
    }
}
