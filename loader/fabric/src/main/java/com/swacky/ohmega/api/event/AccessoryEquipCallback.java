package com.swacky.ohmega.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@link com.swacky.ohmega.api.IAccessory#onEquip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanEquipCallback}
 */
public interface AccessoryEquipCallback {
    Event<AccessoryEquipCallback> EVENT = EventFactory.createArrayBacked(AccessoryEquipCallback.class,
        listeners -> (player, stack, context) -> {
            for (AccessoryEquipCallback listener : listeners) {
                EventResult result = listener.process(player, stack, context);

                if (result.isCanceled()) {
                    return result;
                }
            }
            return EventResult.PASS;
        }
    );

    EventResult process(Player player, ItemStack stack, Context context);

    enum Context {
        GENERIC,
        SLOT_PLACE,
        RIGHT_CLICK_HELD_ITEM
    }
}
