package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface AccessoryCanUnequipCallback {
    Event<AccessoryCanUnequipCallback> EVENT = EventFactory.createArrayBacked(AccessoryCanUnequipCallback.class,
        listeners -> (player, stack, ret) -> {
            for (AccessoryCanUnequipCallback listener : listeners) {
                ret = listener.process(player, stack, ret);
            }
            return ret;
        }
    );

    boolean process(Player player, ItemStack stack, boolean currentReturnValue);
}
