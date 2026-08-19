package com.swacky.ohmega.client.renderer;

public interface LivingEntityRenderStateExtension {
    <T> T ohmega$getData(RenderStateDataKey<T> key);

    <T> void ohmega$setData(RenderStateDataKey<T> key, T value);
}
