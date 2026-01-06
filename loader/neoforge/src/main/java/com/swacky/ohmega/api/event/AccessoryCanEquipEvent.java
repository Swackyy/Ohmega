package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryCanEquipEvent extends Event {
    private final Player player;
    private final ItemStack stack;
    private final EquipContext context;
    private boolean ret;

    public AccessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean flag) {
        this.player = player;
        this.stack = stack;
        this.context = context;
        this.ret = flag;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public EquipContext getContext() {
        return context;
    }

    public boolean getReturnValue() {
        return this.ret;
    }

    public void setReturnValue(boolean value) {
        this.ret = value;
    }
}
