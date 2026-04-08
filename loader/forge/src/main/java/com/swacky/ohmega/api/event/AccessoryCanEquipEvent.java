package com.swacky.ohmega.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryCanEquipEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryCanEquipEvent> BUS = EventBus.create(AccessoryCanEquipEvent.class);

    public final Player player;
    public final ItemStack stack;
    public final EquipContext context;
    public boolean returnValue;

    public AccessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean original) {
        this.player = player;
        this.stack = stack;
        this.context = context;
        this.returnValue = original;
    }
}
