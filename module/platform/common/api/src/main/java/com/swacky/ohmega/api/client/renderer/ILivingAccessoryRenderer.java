package com.swacky.ohmega.api.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jspecify.annotations.NonNull;

/**
 * Accessory renderer interface for generic living entities
 */
public interface ILivingAccessoryRenderer extends IAccessoryRenderer<LivingRenderContext> {
    interface Factory {
        @NonNull ILivingAccessoryRenderer construct(EntityRendererProvider.@NonNull Context providerContext);
    }
}
