package com.swacky.ohmega.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Some common variables you can use in your renderers.
 * Also contains a few shortcuts to the most common methods to reduce verbosity
 */
// todo: add comments
public abstract sealed class AccessoryRenderContext<T extends LivingEntityRenderState, U extends EntityModel<? super T>> permits HumanoidRenderContext, LivingRenderContext {
    public final PoseStack poseStack;
    public final SubmitNodeCollectorWrapper collector;
    public final ItemStack stack;
    public final T state;
    public final U parentModel;
    public final ModelManager modelManager;
    public final int packedLight;

    public AccessoryRenderContext(PoseStack poseStack, SubmitNodeCollectorWrapper collector, ItemStack stack, T state, U parentModel, ModelManager modelManager, int packedLight) {
        this.poseStack = poseStack;
        this.collector = collector;
        this.stack = stack;
        this.state = state;
        this.parentModel = parentModel;
        this.modelManager = modelManager;
        this.packedLight = packedLight;
    }

    public void applyBabyScaling() {
        if (state.isBaby) {
            float scale = 2f / 3;

            poseStack.scale(scale, scale, scale);
        }
    }

    public void ignoreBodyRotation() {
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.bodyRot));
    }

    public void lockToPart(ModelPart part) {
        part.translateAndRotate(poseStack);
    }

    public void offsetToPartCentre(ModelPart part) {
        if (!part.cubes.isEmpty()) {
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float maxZ = -Float.MAX_VALUE;

            for (ModelPart.Cube cube : part.cubes) {
                minX = Math.min(minX, cube.minX);
                minY = Math.min(minY, cube.minY);
                minZ = Math.min(minZ, cube.minZ);
                maxX = Math.max(maxX, cube.maxX);
                maxY = Math.max(maxY, cube.maxY);
                maxZ = Math.max(maxZ, cube.maxZ);
            }

            poseStack.translate(
                    (minX + maxX) / 32,
                    (minY + maxY) / 32,
                    (minZ + maxZ) / 32);
        }
    }

    // Does not actually move to a part directly, it is just offsetting from a part's coords to a face
    public void offsetToPartFace(ModelPart part, Direction face) {
        if (!part.cubes.isEmpty()) {
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float maxZ = -Float.MAX_VALUE;

            for (ModelPart.Cube cube : part.cubes) {
                minX = Math.min(minX, cube.minX);
                minY = Math.min(minY, cube.minY);
                minZ = Math.min(minZ, cube.minZ);
                maxX = Math.max(maxX, cube.maxX);
                maxY = Math.max(maxY, cube.maxY);
                maxZ = Math.max(maxZ, cube.maxZ);
            }

            float xo = (minX + maxX) / 32;
            float yo = (minY + maxY) / 32;
            float zo = (minZ + maxZ) / 32;

            switch (face) {
                case DOWN  -> poseStack.translate(xo, maxY / 16, zo);
                case UP    -> poseStack.translate(xo, minY / 16, zo);
                case NORTH -> poseStack.translate(xo, yo, maxZ / 16);
                case SOUTH -> poseStack.translate(xo, yo, minZ / 16);
                case WEST  -> poseStack.translate(maxX / 16, yo, zo);
                case EAST  -> poseStack.translate(minX / 16, yo, zo);
            }
        }
    }

    public void submitBlock(BlockModelResolver modelResolver, BlockModelRenderState renderState, BlockState blockState, BlockDisplayContext displayContext) {
        modelResolver.update(renderState, blockState, displayContext);
        renderState.submit(poseStack, collector.unwrap(), packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    public void submitCustomGeometry(RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer renderer) {
        collector.next().submitCustomGeometry(poseStack, renderType, renderer);
    }

    public void submitGlint(Model<LivingEntityRenderState> model) {
        if (stack.hasFoil()) {
            submitModel(model, RenderTypes.entityGlint());
        }
    }

    public void submitItem(ItemModelResolver modelResolver, ItemStackRenderState renderState, ItemStack stack) {
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

    public void tryLockToPart(String partName) {
        ModelPart root = parentModel.root();

        if (root.hasChild(partName)) {
            lockToPart(root.getChild(partName));
        }
    }

    public void tryOffsetToPartCentre(String partName) {
        ModelPart root = parentModel.root();

        if (root.hasChild(partName)) {
            offsetToPartCentre(root.getChild(partName));
        }
    }

    public void tryOffsetToPartFace(String partName, Direction face) {
        ModelPart root = parentModel.root();

        if (root.hasChild(partName)) {
            offsetToPartFace(root.getChild(partName), face);
        }
    }
}
