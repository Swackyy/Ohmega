package com.swacky.ohmega.client.renderer;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class AccessoryRenderStateDataImpl implements AccessoryRenderStateData.Service {
    public static final RenderStateDataKey<AccessoryRenderStateData> KEY = new RenderStateDataKey<>(Ohmega.id(ID));

    @Override
    public AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return ((LivingEntityRenderStateExtension) state).ohmega$getData(KEY);
    }

    @Override
    public void setData(LivingEntityRenderState state, AccessoryRenderStateData data) {
        ((LivingEntityRenderStateExtension) state).ohmega$setData(KEY, data);
    }
}
