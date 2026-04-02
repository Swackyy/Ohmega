package com.swacky.ohmega.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Some common variables you can use in your renderers.
 * Also contains a few shortcuts to the most common methods to reduce verbosity
 */
public record AccessoryRenderContext(
        PoseStack poseStack,
        SubmitNodeCollectorWrapper collector,
        ItemStack stack,
        LivingEntityRenderState state,
        ModelManager modelManager,
        int packedLight
) {
    public void submitBlock(BlockModelResolver modelResolver, BlockModelRenderState renderState, BlockState blockState, BlockDisplayContext displayContext) {
        modelResolver.update(renderState, blockState, displayContext);
        renderState.submit(poseStack, collector.unwrap(), packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    public void submitCustomGeometry(RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer renderer) {
        collector.next().submitCustomGeometry(poseStack, renderType, renderer);
    }

    public void submitItem(ItemModelResolver modelResolver, ItemStackRenderState renderState) {
        modelResolver.updateForTopItem(renderState, stack, ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 0);
        renderState.submit(poseStack, collector.unwrap(), packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    public void submitModel(Model<LivingEntityRenderState> model, RenderType renderType) {
        collector.next().submitModel(
                model,
                state,
                poseStack,
                renderType,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null);
    }
    public void submitModel(Model<LivingEntityRenderState> model, Identifier textureLocation) {
        collector.next().submitModel(
                model,
                state,
                poseStack,
                textureLocation,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null);
    }


    public void submitModel(Model<LivingEntityRenderState> model, int tintedColor, SpriteId sprite, SpriteGetter sprites) {
        collector.next().submitModel(
                model,
                state,
                poseStack,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                tintedColor,
                sprite,
                sprites,
                state.outlineColor,
                null);
    }
}
