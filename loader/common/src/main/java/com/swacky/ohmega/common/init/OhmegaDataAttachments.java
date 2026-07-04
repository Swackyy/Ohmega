package com.swacky.ohmega.common.init;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.world.entity.LivingEntity;

public final class OhmegaDataAttachments {
    private static final Service SERVICE = Ohmega.loadService(Service.class);

    public static AccessoryData getData(LivingEntity entity) {
        return SERVICE.getData(entity);
    }

    public static void setData(LivingEntity entity, AccessoryData data) {
        SERVICE.setData(entity, data);
    }

    public interface Service {
        AccessoryData getData(LivingEntity entity);

        void setData(LivingEntity entity, AccessoryData data);
    }
}
