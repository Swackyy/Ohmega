package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@link com.swacky.ohmega.api.IAccessory#onEquip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanEquipEvent}
 */
public interface AccessoryEquipEvent {
    Event<AccessoryEquipEvent> EVENT = EventFactory.createArrayBacked(AccessoryEquipEvent.class,
        listeners -> (player, stack, context) -> {
            for (AccessoryEquipEvent listener : listeners) {
                boolean result = listener.process(player, stack, context);

                if (result) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(Player player, ItemStack stack, EquipContext context);
}
