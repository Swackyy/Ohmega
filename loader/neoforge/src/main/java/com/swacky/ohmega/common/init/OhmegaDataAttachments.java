package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.dataattachment.AccessoryInvDataAttachment;
import com.swacky.ohmega.common.OhmegaCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class OhmegaDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, OhmegaCommon.MODID);

    public static final Supplier<AttachmentType<AccessoryInvDataAttachment>> ACCESSORY_HANDLER = register("accessory_handler",
            () -> AttachmentType.builder(AccessoryInvDataAttachment::new)
                    .serialize(AccessoryInvDataAttachment.SERIALIZER)
                    .sync(AccessoryInvDataAttachment.STREAM_CODEC)
                    .build());

    private static <T> Supplier<AttachmentType<T>> register(String id, Supplier<AttachmentType<T>> sup) {
        return ATTACHMENT_TYPES.register(id, sup);
    }

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
