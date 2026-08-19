package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.api.common.Ohmega;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Do not use if your target menu implements {@link IAccessoryMenu} directly
 * <p>
 * A utility class for {@link IAccessoryMenu}s that are implemented via mixins.
 * This adds default dummy method bodies that throw immediately on calling to provide more explanatory exceptions
 */
public interface IMixinAccessoryMenu extends IAccessoryMenu {
    @Override
    @Nullable
    default AccessoryMenuExtension getAccessoryExtension() {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }

    @Override
    default void setAccessoryExtension(@NonNull AccessoryMenuExtension extension) {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }
}
