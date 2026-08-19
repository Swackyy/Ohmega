package com.swacky.ohmega.client.renderer;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public final class AccessoryRenderStateDataImpl implements AccessoryRenderStateData.Service {
    public static final RenderStateDataKey<AccessoryRenderStateData> KEY = new RenderStateDataKey<>(ID);

    @Override
    public AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return ((LivingEntityRenderStateExtension) state).ohmega$getData(KEY);
    }
}
