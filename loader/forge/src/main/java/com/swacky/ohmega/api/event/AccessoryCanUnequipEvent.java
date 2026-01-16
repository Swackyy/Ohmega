package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryCanUnequipEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryCanUnequipEvent> BUS = EventBus.create(AccessoryCanUnequipEvent.class);

    public final Player player;
    public final ItemStack stack;
    public boolean returnValue;

    public AccessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial) {
        this.player = player;
        this.stack = stack;
        this.returnValue = initial;
    }
}
