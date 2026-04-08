package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCanUnequipEvent {
    Event<AccessoryCanUnequipEvent> EVENT = EventFactory.createArrayBacked(AccessoryCanUnequipEvent.class,
        listeners -> (player, stack, ret) -> {
            for (AccessoryCanUnequipEvent listener : listeners) {
                ret = listener.process(player, stack, ret);
            }

            return ret;
        }
    );

    boolean process(Player player, ItemStack stack, boolean original);
}
