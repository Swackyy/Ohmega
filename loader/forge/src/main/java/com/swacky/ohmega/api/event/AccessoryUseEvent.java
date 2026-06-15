package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.NonNull;

public class AccessoryUseEvent extends MutableEvent implements Cancellable {
    public static final CancellableEventBus<@NonNull AccessoryUseEvent> BUS = CancellableEventBus.create(AccessoryUseEvent.class);

    public final Player player;
    public final ItemStack stack;
    private final MutableBoolean shouldSynchronise;

    public AccessoryUseEvent(Player player, ItemStack stack, MutableBoolean shouldSynchronise) {
        this.player = player;
        this.stack = stack;
        this.shouldSynchronise = shouldSynchronise;
    }

    public void setSynchronise() {
        shouldSynchronise.setTrue();
    }
}
