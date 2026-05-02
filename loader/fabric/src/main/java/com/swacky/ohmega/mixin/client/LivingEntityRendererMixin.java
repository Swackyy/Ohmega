package com.swacky.ohmega.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.client.renderer.AccessoryRenderLayer;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateDataImpl;
import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin<T extends LivingEntity, U extends LivingEntityRenderState, V extends EntityModel<? super U>> extends EntityRenderer<T, U> implements RenderLayerParent<U, V> {
    @Shadow
    @Final
    protected List<RenderLayer<U, V>> layers;

    private LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At(
                    value = "TAIL"))
    private void extractRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        state.setData(AccessoryRenderStateDataImpl.KEY, ClientCallbacks.createRenderStateData(entity));
    }

    // todo: move to a common mixin
    @Inject(
            method = "<init>",
            at = @At(
                    value = "TAIL"))
    private void init(EntityRendererProvider.Context context, V model, float shadow, CallbackInfo ci) {
        layers.add(new AccessoryRenderLayer<>(context, this));
    }

    // todo: move to a common mixin
    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo ci) {
        ClientCallbacks.preventRender((LivingEntityRenderState) state, ci);
    }
}
