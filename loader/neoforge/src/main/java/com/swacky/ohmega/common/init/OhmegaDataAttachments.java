package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.OhmegaCommon;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class OhmegaDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, OhmegaCommon.MODID);

    public static final Supplier<AttachmentType<AccessoryContainer>> ACCESSORY_HANDLER = ATTACHMENT_TYPES.register("accessory_handler",
            () -> AttachmentType.serializable(AccessoryContainer::new).build());
}
