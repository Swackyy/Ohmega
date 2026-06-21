package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.common.Ohmega;
import org.jspecify.annotations.NonNull;

/**
 * Do not use if your target menu implements {@link IEntityRenderingScreen} directly
 * <p>
 * A utility class for {@link IEntityRenderingScreen}s that are implemented via mixins.
 * This adds default dummy method bodies that throw immediately on calling to provide more explanatory exceptions
 */
public interface IMixinEntityRenderingScreen extends IEntityRenderingScreen {
    @Override
    default @NonNull LazyPosition getFlipEntityButtonPosition() {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }
}
