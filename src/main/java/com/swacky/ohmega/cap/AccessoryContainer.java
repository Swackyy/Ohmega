package com.swacky.ohmega.cap;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.event.ForgeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessoryContainer extends ItemStackHandler implements IItemHandlerModifiable {
    private static final int SLOTS = 6;
    private final boolean[] changed = new boolean[SLOTS];
    private final ItemStack[] previous = new ItemStack[SLOTS];
    private final LivingEntity entity;
    public AccessoryContainer(LivingEntity entity) {
        super(6);
        this.entity = entity;
        Arrays.fill(this.previous, ItemStack.EMPTY);
    }

    public boolean isValid(int slot, ItemStack stack) {
        var cap = stack.getCapability(Ohmega.ACCESSORY_ITEM);
        if (stack.isEmpty() || !cap.isPresent()) return false;
        var accessory = cap.orElseThrow(NullPointerException::new);
        return accessory.canEquip(entity);// && bauble.getType().hasSlot(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || this.isValid(slot, stack)) super.setStackInSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!this.isValid(slot, stack)) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.changed[slot] = true;
    }

    public void tick() {
        for (int i = 0; i < getSlots(); i++) {
            var stack = getStackInSlot(i);
            stack.getCapability(Ohmega.ACCESSORY_ITEM).ifPresent(b -> b.onEquippedTick(this.entity, stack));
        }
        this.sync();
    }

    private void sync() {
        if (!(entity instanceof ServerPlayer)) return;

        final var holder = (ServerPlayer) this.entity;

        List<ServerPlayer> receivers = null;
        for (byte i = 0; i < getSlots(); i++) {
            final var stack = getStackInSlot(i);
            boolean autoSync = stack.getCapability(Ohmega.ACCESSORY_ITEM).map(a -> a.shouldTickUpdate(this.entity)).orElse(false);
            if (changed[i] || autoSync && !ItemStack.isSame(stack, previous[i])) {
                if (receivers == null) {
                    receivers = new ArrayList<>(((ServerLevel) this.entity.level).getPlayers((serverPlayerEntity) -> true));
                    receivers.add(holder);
                }
                ForgeEvents.syncSlot(holder, i, stack, receivers);
                this.changed[i] = false;
                previous[i] = stack.copy();
            }
        }
    }
}
