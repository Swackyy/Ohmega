package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public interface AccessoryDataAttachEvent {
    Event<AccessoryDataAttachEvent> EVENT = EventFactory.createArrayBacked(AccessoryDataAttachEvent.class,
        listeners -> (data, entity) -> {
            for (AccessoryDataAttachEvent listener : listeners) {
                listener.process(data, entity);
            }
        }
    );

    void process(AccessoryData data, LivingEntity entity);
}
