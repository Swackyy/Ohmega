package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.dataattachment.AccessoryInvDataAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.function.Consumer;

public class OhmegaDataAttachments {
    public static final AttachmentType<AccessoryInvDataAttachment> ACCESSORY_HANDLER = register("accessory_handler",
            builder -> builder
            .initializer(AccessoryInvDataAttachment::new)
            .persistent(AccessoryInvDataAttachment.CODEC)
            .syncWith(AccessoryInvDataAttachment.STREAM_CODEC, AttachmentSyncPredicate.allButTarget())
            .copyOnDeath());

    private static <T> AttachmentType<T> register(String id, Consumer<AttachmentRegistry.Builder<T>> consumer) {
        return AttachmentRegistry.create(OhmegaCommon.id(id), consumer);
    }

    public static void init() {}
}
