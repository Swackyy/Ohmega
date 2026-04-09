package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.IAccessory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onUnequip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanUnequipEvent}
 */
public interface AccessoryUnequipEvent {
    Event<AccessoryUnequipEvent> EVENT = EventFactory.createArrayBacked(AccessoryUnequipEvent.class,
        listeners -> (player, stack) -> {
            for (AccessoryUnequipEvent listener : listeners) {
                if (listener.process(player, stack)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(Player player, ItemStack stack);
}
