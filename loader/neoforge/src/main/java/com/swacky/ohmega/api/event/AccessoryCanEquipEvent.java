package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryCanEquipEvent extends Event {
    public final Player player;
    public final ItemStack stack;
    public final EquipContext context;
    public boolean returnValue;

    public AccessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial) {
        this.player = player;
        this.stack = stack;
        this.context = context;
        this.returnValue = initial;
    }
}
