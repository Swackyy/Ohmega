package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.client.renderer.LivingEntityRenderStateExtension;
import com.swacky.ohmega.client.renderer.RenderStateDataKey;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(LivingEntityRenderState.class)
abstract class LivingEntityRenderStateMixin extends EntityRenderState implements LivingEntityRenderStateExtension {
    @Unique
    private final Map<RenderStateDataKey<?>, Object> ohmega$data = new Reference2ObjectOpenHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T ohmega$getData(RenderStateDataKey<T> key) {
        return (T) ohmega$data.get(key);
    }

    @Override
    public <T> void ohmega$setData(RenderStateDataKey<T> key, T value) {
        ohmega$data.put(key, value);
    }
}
