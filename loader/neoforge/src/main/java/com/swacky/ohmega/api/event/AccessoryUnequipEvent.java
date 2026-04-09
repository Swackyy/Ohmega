package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.IAccessory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onUnequip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanUnequipEvent}
 */
public final class AccessoryUnequipEvent extends Event implements ICancellableEvent {
    public final Player player;
    public final ItemStack stack;

    public AccessoryUnequipEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }
}
