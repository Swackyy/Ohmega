package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.client.renderer.AccessoryRenderLayer;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateDataImpl;
import com.swacky.ohmega.client.renderer.LivingEntityRenderStateExtension;
import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
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
        // todo
        if (entity instanceof AbstractClientPlayer player) {
            ((LivingEntityRenderStateExtension) state).ohmega$setData(AccessoryRenderStateDataImpl.KEY, ClientCallbacks.createRenderStateData(player));
        }
    }

    @Inject(
            method = "<init>",
            at = @At(
                    value = "TAIL"))
    private void init(EntityRendererProvider.Context context, V model, float shadow, CallbackInfo ci) {
        layers.add(new AccessoryRenderLayer<>(context, this));
    }
}
