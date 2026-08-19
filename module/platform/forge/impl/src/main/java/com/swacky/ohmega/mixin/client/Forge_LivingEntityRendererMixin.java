package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.event.ClientCallbacks;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateDataImpl;
import com.swacky.ohmega.client.renderer.LivingEntityRenderStateExtension;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class Forge_LivingEntityRendererMixin<T extends LivingEntity, U extends LivingEntityRenderState, V extends EntityModel<? super U>> extends EntityRenderer<T, U> implements RenderLayerParent<U, V> {

    private Forge_LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At(
                    value = "TAIL"))
    private void extractRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        ((LivingEntityRenderStateExtension) state).ohmega$setData(AccessoryRenderStateDataImpl.KEY, ClientCallbacks.createRenderStateData(entity));
    }
}
