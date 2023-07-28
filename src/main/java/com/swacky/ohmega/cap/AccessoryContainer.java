package com.swacky.ohmega.cap;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.event.ForgeEvents;
import com.swacky.ohmega.network.ModNetworking;
import com.swacky.ohmega.network.S2C.SyncActivePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
    private final Player entity;
    private final boolean[] active = new boolean[3];
    public AccessoryContainer(Player entity) {
        super(SLOTS);
        this.entity = entity;
        Arrays.fill(this.previous, ItemStack.EMPTY);
    }

    public boolean isValid(ItemStack stack) {
        var cap = stack.getCapability(Ohmega.ACCESSORY_ITEM);
        if (stack.isEmpty() || !cap.isPresent()) return false;
        var accessory = cap.orElseThrow(NullPointerException::new);
        return accessory.canEquip(entity);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || this.isValid(stack)) super.setStackInSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!this.isValid(stack)) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.changed[slot] = true;
    }

    public void tick() {
        for (int i = 0; i < getSlots(); i++) {
            var stack = getStackInSlot(i);
            stack.getCapability(Ohmega.ACCESSORY_ITEM).ifPresent(b -> b.tick(this.entity, stack));
        }
        this.sync();
    }

    private void sync() {
        if (!(entity instanceof ServerPlayer)) {
            return;
        }

        final var holder = (ServerPlayer) this.entity;

        List<ServerPlayer> receivers = null;
        for (byte i = 0; i < getSlots(); i++) {
            final var stack = getStackInSlot(i);
            boolean autoSync = stack.getCapability(Ohmega.ACCESSORY_ITEM).map(a -> a.autoSync(this.entity)).orElse(false);
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

    // Calling on client is a no no
    public void setActive(int slot, boolean value) {
        setActive(slot, value, false);
    }

    public void setActive(int slot, boolean value, boolean client) {
        if(slot > 2) {
            if (!client) {
                ModNetworking.sendTo(new SyncActivePacket(this.entity.getId(), this.active), (ServerPlayer) this.entity);
            }
            this.active[slot - 3] = value;
        }
    }

    public void setActive(int slot) {
        setActive(slot, true);
    }

    public void toggle(int slot) {
        if(slot > 2 && !getStackInSlot(slot).isEmpty()) {
            this.setActive(slot, !this.active[slot-3]);
        }
    }

    public boolean isActive(int slot) {
        if(slot < 3) {
            return false;
        }

        return this.active[slot - 3];
    }
}
