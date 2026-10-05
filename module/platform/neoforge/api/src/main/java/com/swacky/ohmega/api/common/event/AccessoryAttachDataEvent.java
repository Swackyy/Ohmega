package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;

public final class AccessoryAttachDataEvent extends Event {
    public final AccessoryData data;
    public final LivingEntity entity;

    public AccessoryAttachDataEvent(AccessoryData data, LivingEntity entity) {
        this.data = data;
        this.entity = entity;
    }
}
