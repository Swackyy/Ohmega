package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.function.Consumer;

public final class OhmegaDataAttachments {
    public static final AttachmentType<AccessoryContainer> ACCESSORY_HANDLER = register("accessory_data",
            builder -> builder
            .initializer(AccessoryContainer::new)
            .persistent(AccessoryContainer.CODEC));

    private static <T> AttachmentType<T> register(String id, Consumer<AttachmentRegistry.Builder<T>> consumer) {
        return AttachmentRegistry.create(OhmegaCommon.id(id), consumer);
    }

    public static void init() {}
}
