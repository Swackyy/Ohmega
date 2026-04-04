package com.swacky.ohmega.client.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public final class AccessoryRenderStateDataImpl implements AccessoryRenderStateData.Service {
    public static final RenderStateDataKey<AccessoryRenderStateData> KEY = RenderStateDataKey.create(ID::toString);

    @Override
    public AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return state.getData(KEY);
    }
}
