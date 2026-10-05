package com.swacky.ohmega.client.renderer;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.mixinduck.client.LivingEntityRenderStateExtension;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class AccessoryRenderStateDataImpl implements AccessoryRenderStateData.Service {
    public static final RenderStateDataKey<AccessoryRenderStateData> KEY = new RenderStateDataKey<>(ID);

    @Override
    public @Nullable AccessoryRenderStateData getData(@NonNull LivingEntityRenderState state) {
        return ((LivingEntityRenderStateExtension) state).ohmega$getData(KEY);
    }
}
