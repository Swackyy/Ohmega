package com.swacky.ohmega.api.common.item;

import com.swacky.ohmega.common.OhmegaMain;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public @NonNull AccessoryData getData(@NonNull LivingEntity entity) {
        return entity.getCapability(OhmegaMain.ACCESSORIES).orElseThrow(() ->
                new NullPointerException("Accessory data fetched on entity '" + entity.getPlainTextName() + "' is not present"));
    }

    public static boolean isPlayerDataPresent(LivingEntity entity) {
        return entity.getCapability(OhmegaMain.ACCESSORIES).isPresent();
    }
}
