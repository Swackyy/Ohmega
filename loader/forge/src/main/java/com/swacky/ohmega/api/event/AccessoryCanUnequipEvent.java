package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryCanUnequipEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryCanUnequipEvent> BUS = EventBus.create(AccessoryCanUnequipEvent.class);

    private final Player player;
    private final ItemStack stack;
    private boolean ret;

    public AccessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        this.player = player;
        this.stack = stack;
        this.ret = flag;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean getReturnValue() {
        return this.ret;
    }

    public void setReturnValue(boolean value) {
        this.ret = value;
    }
}
