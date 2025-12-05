package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted for every ticking accessory in the accessory inventory.
 * <p>
 * Posted before the tick
 */
@SuppressWarnings("removal")
public interface AccessoryTickPreCallback {
    Event<AccessoryTickPreCallback> EVENT = EventFactory.createArrayBacked(AccessoryTickPreCallback.class,
        listeners -> (player, stack) -> {
            for (AccessoryTickPreCallback listener : listeners) {
                EventResult result = listener.process(player, stack);

                if (result.isCanceled()) {
                    return result;
                }
            }
            return EventResult.PASS;
        }
    );

    EventResult process(Player player, ItemStack stack);
}
