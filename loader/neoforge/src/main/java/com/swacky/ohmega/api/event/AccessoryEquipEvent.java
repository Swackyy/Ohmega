package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@link com.swacky.ohmega.api.IAccessory#onEquip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanEquipEvent}
 */

public class AccessoryEquipEvent extends Event implements ICancellableEvent {
    private final Player player;
    private final ItemStack stack;
    private final Context context;

    public AccessoryEquipEvent(Player player, ItemStack stack, Context context) {
        this.player = player;
        this.stack = stack;
        this.context = context;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public Context getContext() {
        return this.context;
    }

    public enum Context {
        GENERIC,
        SLOT_PLACE,
        RIGHT_CLICK_HELD_ITEM
    }
}
