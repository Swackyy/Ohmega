package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.dataattachment.AccessoryInvDataAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class OhmegaDataAttachments {
    public static final AttachmentType<AccessoryInvDataAttachment> ACCESSORY_HANDLER = AttachmentRegistry.create(OhmegaCommon.rl("accessory_handler"),
            builder -> builder
            .persistent(AccessoryInvDataAttachment.CODEC)
            .copyOnDeath()
            .initializer(AccessoryInvDataAttachment::new));

    public static void init() {}
}
