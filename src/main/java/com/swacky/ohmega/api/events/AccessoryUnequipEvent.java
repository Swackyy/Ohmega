package com.swacky.ohmega.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is posted when an accessory is unequipped
 * <p>
 * Cancelling only cancels overrides of {@code IAccessory.onUnequip()} and does not stop the accessory from being unequipped
 */
@Cancelable
public class AccessoryUnequipEvent extends Event {
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
