package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NonNull;

public record AccessoryAttachDataEvent(AccessoryData data, LivingEntity entity) implements RecordEvent {
    public static final EventBus<@NonNull AccessoryAttachDataEvent> BUS = EventBus.create(AccessoryAttachDataEvent.class);
}
