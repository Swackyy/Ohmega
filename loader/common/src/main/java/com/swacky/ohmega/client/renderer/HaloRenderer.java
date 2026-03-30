package com.swacky.ohmega.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.client.renderer.IAccessoryRenderer;
import com.swacky.ohmega.client.model.HaloModel;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class HaloRenderer implements IAccessoryRenderer {
    private static final Identifier HALO_LOCATION = Ohmega.id("textures/accessory/halo.png");

    private final HaloModel model;

    public HaloRenderer(EntityRendererProvider.Context context) {
        this.model = new HaloModel(context.bakeLayer(HaloModel.LOCATION), RenderTypes::entitySolid);
    }

    @Override
    public void submit(AccessoryRenderContext context) {
        PoseStack stack = context.poseStack();
        LivingEntityRenderState state = context.state();

        stack.pushPose();

        // Align with head rotation
        stack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        stack.mulPose(Axis.XP.rotationDegrees(state.xRot));

        // Move slightly above head
        stack.translate(0, -state.eyeHeight - 0.45, 0);

        // Account for items worn on the head
        if (!state.headItem.isEmpty()) {
            // Non-helmet items
            stack.translate(0, -state.headItem.getModelBoundingBox().getYsize() / 16, 0);
        } else if (state instanceof AvatarRenderState state0 && !state0.headEquipment.isEmpty()) {
            // Helmets
            stack.translate(0, -1f/16, 0);
        }

        // Render textured model
        context.submitModel(model, HALO_LOCATION, 1);

        stack.popPose();
    }
}
