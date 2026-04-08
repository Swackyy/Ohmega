package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCanEquipEvent {
    Event<AccessoryCanEquipEvent> EVENT = EventFactory.createArrayBacked(AccessoryCanEquipEvent.class,
        listeners -> (player, stack, context, ret) -> {
            for (AccessoryCanEquipEvent listener : listeners) {
                ret = listener.process(player, stack, context, ret);
            }

            return ret;
        }
    );

    boolean process(Player player, ItemStack stack, EquipContext context, boolean original);
}
