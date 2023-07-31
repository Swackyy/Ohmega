package com.swacky.ohmega.cap;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.event.ForgeEvents;
import com.swacky.ohmega.network.ModNetworking;
import com.swacky.ohmega.network.S2C.SyncActivePacket;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

// Since this is an internal class, please refrain from calling sided methods incorrectly as it may be unsafe and lead to crashes
public class AccessoryContainer extends ItemStackHandler implements IItemHandlerModifiable {
    private static final int SLOTS = 6;
    private final boolean[] changed = new boolean[SLOTS];
    private final NonNullList<ItemStack> previous = NonNullList.withSize(6, ItemStack.EMPTY);
    private final Player player;
    private final boolean[] active = new boolean[3];
    public AccessoryContainer(Player player) {
        super(SLOTS);
        this.player = player;
    }

    public boolean isValid(ItemStack stack) {
        var cap = stack.getCapability(Ohmega.ACCESSORY_ITEM);
        if (stack.isEmpty() || !cap.isPresent()) return false;
        var accessory = cap.orElseThrow(NullPointerException::new);
        return accessory.canEquip(player);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if(stack.isEmpty() || this.isValid(stack) && AccessoryHelper.isExclusiveType(player, stack)) {
            super.setStackInSlot(slot, stack);
        }
    }

    // Workaround to make a boolean return
    public boolean trySetStackInSlot(int slot, ItemStack stack) {
        if((stack.isEmpty() || this.isValid(stack)) && AccessoryHelper.isExclusiveType(player, stack)) {
            super.setStackInSlot(slot, stack);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(!this.isValid(stack)) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.changed[slot] = true;
    }

    public void tick() {
        for(int i = 0; i < getSlots(); i++) {
            var stack = getStackInSlot(i);
            stack.getCapability(Ohmega.ACCESSORY_ITEM).ifPresent(b -> b.tick(this.player, stack));
        }
        this.sync();
    }

    private void sync() {
        if(this.player instanceof final ServerPlayer svr) {
            List<ServerPlayer> receivers = null;
            for (byte i = 0; i < getSlots(); i++) {
                final ItemStack stack = getStackInSlot(i);
                boolean autoSync = stack.getCapability(Ohmega.ACCESSORY_ITEM).map(a -> a.autoSync(this.player)).orElse(false);
                if (this.changed[i] || autoSync && !ItemStack.isSame(stack, this.previous.get(i))) {
                    if (receivers == null) {
                        receivers = new ArrayList<>(((ServerLevel) this.player.level).getPlayers((svr0) -> true));
                        receivers.add(svr);
                    }
                    ForgeEvents.syncSlot(svr, i, stack, receivers);
                    for(ServerPlayer player : receivers) {
                        ModNetworking.sendTo(new SyncActivePacket(svr.getId(), this.active), player);
                    }
                    this.changed[i] = false;
                    this.previous.set(i, stack.copy());
                }
            }
        }
    }

    public void setActive(int slot, boolean value, boolean client) {
        if(slot > 2) {
            this.active[slot - 3] = value;
            if(!client) {
                ModNetworking.sendTo(new SyncActivePacket(this.player.getId(), this.active), (ServerPlayer) this.player);
            } else AccessoryHelper.addActiveTag(getStackInSlot(slot), value);
        }
    }

    // Calling on client is a no no
    public void setActive(int slot, boolean value) {
        setActive(slot, value, false);
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

    @Override
    public void deserializeNBT(CompoundTag tag) {
        super.deserializeNBT(tag);
        this.active[0] = getStackInSlot(3).getOrCreateTag().getBoolean("active");
        this.active[1] = getStackInSlot(4).getOrCreateTag().getBoolean("active");
        this.active[2] = getStackInSlot(5).getOrCreateTag().getBoolean("active");
    }

    public boolean[] getActive() {
        return this.active;
    }
}
