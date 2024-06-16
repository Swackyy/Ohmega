package com.swacky.ohmega.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@code IAccessory.onEquip()} and does not stop the accessory from being equipped
 */
@Cancelable
public class AccessoryEquipEvent extends Event {
    private final Player player;
    private final ItemStack stack;

    public AccessoryEquipEvent(Player player, ItemStack stack) {
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
