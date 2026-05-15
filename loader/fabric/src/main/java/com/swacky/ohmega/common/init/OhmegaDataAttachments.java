package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.function.Consumer;

public final class OhmegaDataAttachments {
    public static final AttachmentType<AccessoryData> ACCESSORY = register("accessory_data", builder -> builder
            .initializer(AccessoryData::new)
            .persistent(AccessoryData.CODEC));

    private static <T> AttachmentType<T> register(String id, Consumer<AttachmentRegistry.Builder<T>> consumer) {
        return AttachmentRegistry.create(Ohmega.id(id), consumer);
    }

    public static void init() {}
}
