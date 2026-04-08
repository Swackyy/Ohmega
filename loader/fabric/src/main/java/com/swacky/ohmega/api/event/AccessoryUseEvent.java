package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface AccessoryUseEvent {
    Event<AccessoryUseEvent> EVENT = EventFactory.createArrayBacked(AccessoryUseEvent.class,
        listeners -> (player, stack) -> {
            for (AccessoryUseEvent listener : listeners) {
                if (listener.process(player, stack)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(Player player, ItemStack stack);
}
