package com.swacky.ohmega.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record AccessoryRenderContext(
        PoseStack poseStack,
        SubmitNodeCollector collector,
        ItemStack stack,
        LivingEntityRenderState state,
        ModelManager modelManager,
        int packedLight
) {
    public void submitCustomGeometry(RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer renderer, int order) {
        collector.order(order).submitCustomGeometry(poseStack, renderType, renderer);
    }

    public void submitModel(Model<LivingEntityRenderState> model, Identifier textureLocation, int order) {
        collector.order(order).submitModel(
                model,
                state,
                poseStack,
                textureLocation,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null);
    }

    public void submitModel(Model<LivingEntityRenderState> model, Identifier textureLocation) {
        submitModel(model, textureLocation, 1);
    }
}
