package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class OhmegaDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ohmega.MODID);

    public static final Supplier<AttachmentType<AccessoryData>> ACCESSORY_HANDLER = register("accessory_data",
            () -> AttachmentType.builder(AccessoryData::new)
                    .serialize(AccessoryData.MAP_CODEC)
                    .build());

    private static <T> Supplier<AttachmentType<T>> register(String id, Supplier<AttachmentType<T>> sup) {
        return ATTACHMENT_TYPES.register(id, sup);
    }

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
