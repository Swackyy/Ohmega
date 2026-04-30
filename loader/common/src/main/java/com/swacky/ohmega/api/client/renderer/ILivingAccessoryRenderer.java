package com.swacky.ohmega.api.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public interface ILivingAccessoryRenderer extends IAccessoryRenderer<LivingRenderContext> {
    interface Factory {
        ILivingAccessoryRenderer construct(EntityRendererProvider.Context providerContext);
    }
}
