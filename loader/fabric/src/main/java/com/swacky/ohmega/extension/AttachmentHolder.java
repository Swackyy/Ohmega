package com.swacky.ohmega.extension;

import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import org.jspecify.annotations.NonNull;

public interface AttachmentHolder {
    @NonNull AccessoryContainer ohmega$getContainer();

    void ohmega$setContainer(AccessoryContainer container);
}
