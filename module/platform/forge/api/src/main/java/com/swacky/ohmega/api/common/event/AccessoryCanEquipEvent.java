package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.item.EquipContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryCanEquipEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryCanEquipEvent> BUS = EventBus.create(AccessoryCanEquipEvent.class);

    public final LivingEntity entity;
    public final ItemStack stack;
    public final EquipContext context;
    public boolean returnValue;

    public AccessoryCanEquipEvent(LivingEntity entity, ItemStack stack, EquipContext context, boolean original) {
        this.entity = entity;
        this.stack = stack;
        this.context = context;
        this.returnValue = original;
    }
}
