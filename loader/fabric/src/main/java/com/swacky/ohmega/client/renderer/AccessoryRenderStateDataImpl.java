package com.swacky.ohmega.client.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class AccessoryRenderStateDataImpl implements AccessoryRenderStateData.Service {
    public static final RenderStateDataKey<AccessoryRenderStateData> RENDER_STATE_DATA_KEY = RenderStateDataKey.create(() -> "accessory_stacks");

    @Override
    public AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return state.getData(RENDER_STATE_DATA_KEY);
    }
}
