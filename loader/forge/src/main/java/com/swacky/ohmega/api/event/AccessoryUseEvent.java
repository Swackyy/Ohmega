package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NonNull;

public record AccessoryUseEvent(Player player, ItemStack stack) implements RecordEvent, Cancellable {
    public static final CancellableEventBus<@NonNull AccessoryUseEvent> BUS = CancellableEventBus.create(AccessoryUseEvent.class);

}
