package com.swacky.ohmega.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.renderer.ILivingAccessoryRenderer;
import com.swacky.ohmega.api.client.renderer.LivingRenderContext;
import com.swacky.ohmega.client.model.HaloModel;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * A renderer for the {@link AngelRing} item.
 * Works fairly well on any living entity with a direct {@code "head"} child part, but does not dilate in account for outer layers,
 * and so will appear closer to the top of the head on a sheep for example
 */
public class HaloRenderer implements ILivingAccessoryRenderer {
    private static final Identifier HALO_LOCATION = Ohmega.id("textures/accessory/halo.png");

    private final HaloModel model;

    public HaloRenderer(EntityRendererProvider.Context context) {
        this.model = new HaloModel(context.bakeLayer(HaloModel.LOCATION), RenderTypes::entitySolid);
    }

    @Override
    public void submit(@NonNull LivingRenderContext context) {
        if (!context.state.isInvisible) {
            ModelPart root = context.parentModel.root();

            if (root.hasChild(PartNames.HEAD)) {
                PoseStack stack = context.poseStack;
                LivingEntityRenderState state = context.state;
                ModelPart head = root.getChild(PartNames.HEAD);

                stack.pushPose();

                // Align to head top face and follow its rotation
                context.lockToPart(head);
                context.offsetToPartFace(head, Direction.UP);

                // Account for baby entities
                context.applyAgeScaling();

                float ageFactor = state.ageScale;

                // Move above by 1px
                stack.translate(0, -1f / 16 * ageFactor, 0);

                // Account for items worn on the head
                if (!state.headItem.isEmpty()) {
                    // Non-helmet items
                    stack.translate(0, -state.headItem.getModelBoundingBox().getYsize() / 16 * ageFactor, 0);
                } else if (state instanceof AvatarRenderState avatarState && !avatarState.headEquipment.isEmpty()) {
                    // Helmets
                    stack.translate(0, -1f / 16 * ageFactor, 0);
                }

                // Render textured model
                context.submitModel(model, HALO_LOCATION);
                context.submitGlint(model);

                stack.popPose();
            }
        }
    }
}
