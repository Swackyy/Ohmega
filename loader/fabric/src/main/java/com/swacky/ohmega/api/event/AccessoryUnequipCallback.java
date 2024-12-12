package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@link com.swacky.ohmega.api.IAccessory#onUnequip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanUnequipCallback}
 */
public interface AccessoryUnequipCallback {
    Event<AccessoryUnequipCallback> EVENT = EventFactory.createArrayBacked(AccessoryUnequipCallback.class,
        listeners -> (player, stack) -> {
            for (AccessoryUnequipCallback listener : listeners) {
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
