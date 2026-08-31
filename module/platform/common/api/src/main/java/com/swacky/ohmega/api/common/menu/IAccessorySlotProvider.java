package com.swacky.ohmega.api.common.menu;

import org.jspecify.annotations.NonNull;

/**
 * A provider interface for {@link AccessorySlot}s
 */
public interface IAccessorySlotProvider {
    @NonNull AccessorySlot getAccessorySlot();
}
