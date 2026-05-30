package com.swacky.ohmega.api.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Accessory renderer interface for generic living entities
 */
public interface ILivingAccessoryRenderer extends IAccessoryRenderer<LivingRenderContext> {
    interface Factory {
        ILivingAccessoryRenderer construct(EntityRendererProvider.Context providerContext);
    }
}
