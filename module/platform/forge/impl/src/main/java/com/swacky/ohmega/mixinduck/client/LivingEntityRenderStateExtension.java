package com.swacky.ohmega.mixinduck.client;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.client.renderer.RenderStateDataKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface LivingEntityRenderStateExtension {
    default <T> @Nullable T ohmega$getData(@NonNull RenderStateDataKey<T> key) {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }

    default <T> void ohmega$setData(@NonNull RenderStateDataKey<T> key, @NonNull T value) {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }
}
