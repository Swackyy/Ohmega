package com.swacky.ohmega.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class AccessoryCanEquipEvent extends Event {
    private final Player player;
    private final ItemStack stack;

    private boolean ret;

    public AccessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
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

    public void setReturnValue(boolean value) {
        this.ret = value;
    }

    public boolean getReturnValue() {
        return this.ret;
    }
}
