package com.swacky.ohmega.common.init;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class OhmegaDataAttachmentsImpl implements OhmegaDataAttachments.Service {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ohmega.MODID);

    public static final Supplier<AttachmentType<AccessoryData>> ACCESSORY = register("accessory_data", () ->
            AttachmentType.builder(AccessoryData::new)
                    .serialize(AccessoryData.MAP_CODEC)
                    .build());

    private static <T> Supplier<AttachmentType<T>> register(String id, Supplier<AttachmentType<T>> supplier) {
        return ATTACHMENT_TYPES.register(id, supplier);
    }

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    @Override
    public AccessoryData getData(LivingEntity entity) {
        return entity.getData(ACCESSORY);
    }

    @Override
    public void setData(LivingEntity entity, AccessoryData data) {
        entity.setData(ACCESSORY, data);
    }
}
