package com.swacky.ohmega.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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

    /**
     * Apply a generic scaling for baby entities.
     * A factor of {@code 4/3} is used as the vanilla one regularly appears too small
     */
    public void applyAgeScaling() {
        if (state.isBaby) {
            float scaleFactor = state.ageScale * 4 / 3;

            poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        }
    }

    /**
     * Prevents the rendered accessory from rotating with the body rotation
     */
    public void ignoreBodyRotation() {
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.bodyRot));
    }

    /**
     * Sets coordinates to the origin of the provided {@link ModelPart} and follows its relative rotation
     * @param part piece of the model to lock onto
     */
    public void lockToPart(ModelPart part) {
        part.translateAndRotate(poseStack);
    }

    /**
     * Moves to the direct centre of the provided {@link ModelPart} by averaging the distances between all child cubes of the part.
     * This should be called after calling {@link #lockToPart(ModelPart)} on the same part, as it is only a relative offset
     * @param part piece of the model to offset to its centre
     */
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

    /**
     * Moves to the provided directional cube face of the provided {@link ModelPart} by averaging the distances between all child cubes of the part.
     * This should be called after calling {@link #lockToPart(ModelPart)} on the same part, as it is only a relative offset
     * @param part piece of the model to offset to its given face
     * @param face cube face of the {@link ModelPart} to offset to
     */
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

    /**
     * Submit a block to render
     * @param modelResolver lookup for block models, provided in your constructor with {@link EntityRendererProvider.Context#getBlockModelResolver()}
     * @param renderState data holder for your model state. Should be stored as a member variable in your renderer
     * @param blockState the block's in-world {@link BlockState} providing state data to render with
     * @param displayContext should be stored as a member variable in your renderer
     */
    public void submitBlock(BlockModelResolver modelResolver, BlockModelRenderState renderState, BlockState blockState, BlockDisplayContext displayContext) {
        modelResolver.update(renderState, blockState, displayContext);
        renderState.submit(poseStack, collector.unwrap(), packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    /**
     * Submit a piece of custom geometry to render
     * @param renderType render pipeline to use
     * @param renderer actual renderer for the geometry
     */
    public void submitCustomGeometry(RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer renderer) {
        collector.next().submitCustomGeometry(poseStack, renderType, renderer);
    }

    /**
     * Apply enchantment glint on the provided model
     * @param model model to apply glint to
     */
    public void submitGlint(Model<LivingEntityRenderState> model) {
        if (stack.hasFoil()) {
            submitModel(model, RenderTypes.entityGlint());
        }
    }

    /**
     * Submit an item to render
     * @param modelResolver lookup for item models, provided in your constructor with {@link EntityRendererProvider.Context#getItemModelResolver()}
     * @param renderState data holder for your model state. Should be stored as a member variable in your renderer
     * @param stack the item's in-world {@link ItemStack} providing component data to render with
     */
    public void submitItem(ItemModelResolver modelResolver, ItemStackRenderState renderState, ItemStack stack) {
        modelResolver.updateForTopItem(renderState, stack, ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 0);
        renderState.submit(poseStack, collector.unwrap(), packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    /**
     * Submit a generic model for rendering
     * @param model model to render
     * @param renderType render pipeline to use
     */
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

    /**
     * Submit a generic textured model for rendering
     * @param model model to render
     * @param textureLocation resource location for the texture. Should be something like {@code "textures/accessory/my_model.png"}
     */
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


    /**
     * Submit a generic textured model for rendering
     * @param model model to render
     * @param tintedColor colour to tint with
     * @param sprite identifier for the sprite to use
     * @param sprites lookup for sprites
     */
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

    /**
     * Attempts to call {@link #lockToPart(ModelPart)} by lookup of the {@link ModelPart} through the root's immediate children by name
     * @param partName serialised name of the part to lookup. Searching in {@link PartNames} may be useful
     */
    public void tryLockToPart(String partName) {
        ModelPart root = parentModel.root();

        if (root.hasChild(partName)) {
            lockToPart(root.getChild(partName));
        }
    }

    /**
     * Attempts to call {@link #offsetToPartCentre(ModelPart)} by lookup of the {@link ModelPart} through the root's immediate children by name
     * @param partName serialised name of the part to lookup. Searching in {@link PartNames} may be useful
     */
    public void tryOffsetToPartCentre(String partName) {
        ModelPart root = parentModel.root();

        if (root.hasChild(partName)) {
            offsetToPartCentre(root.getChild(partName));
        }
    }

    /**
     * Attempts to call {@link #offsetToPartFace(ModelPart, Direction)} by lookup of the {@link ModelPart} through the root's immediate children by name
     * @param partName serialised name of the part to lookup. Searching in {@link PartNames} may be useful
     * @param face cube face of the {@link ModelPart} to offset to
     */
    public void tryOffsetToPartFace(String partName, Direction face) {
        ModelPart root = parentModel.root();

        if (root.hasChild(partName)) {
            offsetToPartFace(root.getChild(partName), face);
        }
    }
}
