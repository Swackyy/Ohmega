package com.swacky.ohmega.api.client.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;

public final class AccessoryRenderStateDataImpl implements AccessoryRenderStateData.Service {
    public static final ContextKey<AccessoryRenderStateData> KEY = new ContextKey<>(ID);

    @Override
    public AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return state.getRenderData(KEY);
    }
}
