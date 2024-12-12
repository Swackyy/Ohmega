package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.dataattachment.AccessoryInvDataAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class OhmegaDataAttachments {
    public static final AttachmentType<AccessoryInvDataAttachment> ACCESSORY_HANDLER = AttachmentRegistry.<AccessoryInvDataAttachment>builder()
            .persistent(AccessoryInvDataAttachment.CODEC)
            .copyOnDeath()
            .initializer(AccessoryInvDataAttachment::new)
            .buildAndRegister(OhmegaCommon.rl("accessory_handler"));

    public static void init() {}
}
