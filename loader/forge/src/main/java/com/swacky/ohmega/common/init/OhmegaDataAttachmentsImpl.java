package com.swacky.ohmega.common.init;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class OhmegaDataAttachmentsImpl implements OhmegaDataAttachments.Service {
    public static final Capability<AccessoryData> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>() {});

    @Override
    public AccessoryData getData(LivingEntity entity) {
        return entity.getCapability(ACCESSORIES).orElseThrow(() ->
                new NullPointerException("Accessory data fetched for entity '" + entity.getPlainTextName() + "' is not present"));
    }

    @Override
    public void setData(LivingEntity entity, AccessoryData data) {
        getData(entity).copyFrom(data, false);
    }

    public static boolean isAccessoryDataPresent(LivingEntity entity) {
        return entity.getCapability(ACCESSORIES).isPresent();
    }
}
