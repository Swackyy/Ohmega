package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface AccessoryUseCallback {
    Event<AccessoryUseCallback> EVENT = EventFactory.createArrayBacked(AccessoryUseCallback.class,
            listeners -> (player, stack) -> {
                for (AccessoryUseCallback listener : listeners) {
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
