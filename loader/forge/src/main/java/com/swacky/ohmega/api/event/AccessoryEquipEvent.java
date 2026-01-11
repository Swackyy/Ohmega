package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@link com.swacky.ohmega.api.IAccessory#onEquip(Player, ItemStack)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanEquipEvent}
 */
@Cancelable
public class AccessoryEquipEvent extends Event {
    public final Player player;
    public final ItemStack stack;
    public final EquipContext context;

    public AccessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        this.player = player;
        this.stack = stack;
        this.context = context;
    }
}
