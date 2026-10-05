package com.swacky.ohmega.client.renderer;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;
import org.jspecify.annotations.NonNull;

public final class AccessoryRenderStateDataImpl implements AccessoryRenderStateData.Service {
    public static final ContextKey<AccessoryRenderStateData> KEY = new ContextKey<>(ID);

    @Override
    public AccessoryRenderStateData getData(@NonNull LivingEntityRenderState state) {
        return state.getRenderData(KEY);
    }
}
