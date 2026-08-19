package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.LivingEntity;

public final class OhmegaDataAttachments {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static AccessoryData getData(LivingEntity entity) {
        return IMPL.getData(entity);
    }

    public static void setData(LivingEntity entity, AccessoryData data) {
        IMPL.setData(entity, data);
    }

    public interface Service {
        AccessoryData getData(LivingEntity entity);

        void setData(LivingEntity entity, AccessoryData data);
    }
}
