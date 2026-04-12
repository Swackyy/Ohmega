package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.api.IAccessory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * This event is posted when an accessory is equipped
 * <p>
 * Cancelling only cancels overrides of {@link IAccessory#onEquip(Player, ItemStack, EquipContext)} and does not stop the accessory from being equipped;
 * Instead, to achieve such behaviour, use {@link AccessoryCanEquipEvent}
 */

public final class AccessoryEquipEvent extends Event implements ICancellableEvent {
    public final LivingEntity entity;
    public final ItemStack stack;
    public final EquipContext context;

    public AccessoryEquipEvent(LivingEntity entity, ItemStack stack, EquipContext context) {
        this.entity = entity;
        this.stack = stack;
        this.context = context;
    }
}
