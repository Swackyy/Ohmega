package com.swacky.ohmega.api;

import com.swacky.ohmega.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.world.entity.LivingEntity;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public AccessoryData getData(LivingEntity entity) {
        return entity.getAttachedOrCreate(OhmegaDataAttachments.ACCESSORY_HANDLER);
    }
}
