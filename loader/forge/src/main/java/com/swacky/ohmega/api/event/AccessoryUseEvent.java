package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.NotNull;

public class AccessoryUseEvent extends MutableEvent implements Cancellable {
    public static final CancellableEventBus<@NotNull AccessoryUseEvent> BUS = CancellableEventBus.create(AccessoryUseEvent.class);

    private final Player player;
    private final ItemStack stack;

    public AccessoryUseEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
