package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Posted after the tick
 */
public interface AccessoryTickPostCallback {
    Event<AccessoryTickPostCallback> EVENT = EventFactory.createArrayBacked(AccessoryTickPostCallback.class,
        listeners -> (player, stack) -> {
            for (AccessoryTickPostCallback listener : listeners) {
                listener.process(player, stack);
            }
        }
    );

    void process(Player player, ItemStack stack);
}
