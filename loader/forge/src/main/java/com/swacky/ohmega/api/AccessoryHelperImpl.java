package com.swacky.ohmega.api;

import com.swacky.ohmega.common.OhmegaMain;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.LivingEntity;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public AccessoryData getData(LivingEntity entity) {
        return entity.getCapability(OhmegaMain.ACCESSORIES).orElseThrow(() ->
                new NullPointerException("Accessory data fetched on entity '" + entity.getPlainTextName() + "' is not present"));
    }

    public static boolean isPlayerDataPresent(LivingEntity entity) {
        return entity.getCapability(OhmegaMain.ACCESSORIES).isPresent();
    }
}
