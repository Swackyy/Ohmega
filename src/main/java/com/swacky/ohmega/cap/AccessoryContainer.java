package com.swacky.ohmega.cap;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.events.AccessoryTickEvent;
import com.swacky.ohmega.event.ForgeEvents;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.core.NonNullList;
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
// todo: Use onContentsChanged method
public class AccessoryContainer extends ItemStackHandler implements IItemHandlerModifiable {
    private static final int SLOTS = 6;
    private final boolean[] changed = new boolean[SLOTS];
    private final NonNullList<ItemStack> previous = NonNullList.withSize(6, ItemStack.EMPTY);
    private final Player player;
    public AccessoryContainer(Player player) {
        super(SLOTS);
        this.player = player;
    }

    public boolean isValid(ItemStack stack) {
            if (stack.getItem() instanceof IAccessory acc) {
                return OhmegaHooks.accessoryCanEquipEvent(player, stack, acc.canEquip(player, stack)).getReturnValue();
            }
        return false;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if(stack.isEmpty() || this.isValid(stack) && stack.getItem() instanceof IAccessory) {
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

    public void tick() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if(stack.getItem() instanceof IAccessory a) {
                AccessoryTickEvent event = OhmegaHooks.accessoryTickEventPre(this.player, stack);
                if (!event.isCanceled()) {
                    a.tick(this.player, stack);
                    OhmegaHooks.accessoryTickEventPost(this.player, stack);
                }
            }
        }
        this.sync();
    }

    private void sync() {
        if(this.player instanceof final ServerPlayer svr) {
            List<ServerPlayer> receivers = null;
            for (byte i = 0; i < getSlots(); i++) {
                final ItemStack stack = getStackInSlot(i);
                boolean autoSync = false;
                if(stack.getItem() instanceof IAccessory a) {
                    autoSync = a.autoSync(this.player);
                }
                if (this.changed[i] || autoSync && !ItemStack.isSameItem(stack, this.previous.get(i))) {
                    if (receivers == null) {
                        receivers = new ArrayList<>(((ServerLevel) this.player.level()).getPlayers((svr0) -> true));
                        receivers.add(svr);
                    }
                    ForgeEvents.syncSlot(svr, i, stack, receivers);
                    this.changed[i] = false;
                    this.previous.set(i, stack.copy());
                }
            }
        }
    }
}
