package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Do not use if your target menu implements {@link IAccessoryScreen} directly
 * <p>
 * A utility class for {@link IAccessoryScreen}s that are implemented via mixins.
 * This adds default dummy method bodies that throw immediately on calling to provide more explanatory exceptions
 */
public interface IMixinAccessoryScreen extends IAccessoryScreen {
    @Override
    @Nullable
    default AccessoryScreenExtension getAccessoryExtension() {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }

    @Override
    default void setAccessoryExtension(@NonNull AccessoryScreenExtension extension) {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }

    @Override
    default IntIntPair getAccessoryExtensionToggleButtonPosition(OhmegaConfig.Client.Service.ButtonStyle style) {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }
}
