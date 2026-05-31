package com.swacky.ohmega.api.common.item;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public @NonNull AccessoryData getData(@NonNull LivingEntity entity) {
        return entity.getAttachedOrCreate(OhmegaDataAttachments.ACCESSORY);
    }
}
