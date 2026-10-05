package com.swacky.ohmega.mixinduck;

import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public interface DisplayInfoExtension {
    default void ohmega$setAdvancementId(@NonNull Identifier id) {
        throw new IllegalStateException(Ohmega.MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE);
    }
}
