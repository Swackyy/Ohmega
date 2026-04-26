package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.menu.AccessorySlot;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * An interface for mixins to implement in order to expose the accessory slots added by an accessory extension.
 * You most likely won't need to use this, but Ohmega uses it internally for the creative accessory inventory implementation
 * and so it has been placed in the {@code api} package
 */
public interface IAccessorySlotContainer {
    default @Nullable List<AccessorySlot> getAccessoryExtensionSlots() {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }
}
