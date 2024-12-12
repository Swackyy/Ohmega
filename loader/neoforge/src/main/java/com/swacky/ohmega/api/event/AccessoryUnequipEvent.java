package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@link com.swacky.ohmega.api.IAccessory#onUnequip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanUnequipEvent}
 */
public class AccessoryUnequipEvent extends Event implements ICancellableEvent {
    private final Player player;
    private final ItemStack stack;

    public AccessoryUnequipEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
