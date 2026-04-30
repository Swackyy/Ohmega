package com.swacky.ohmega.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemStack;

/**
 * An extension of the default render context, provided with {@link ILivingAccessoryRenderer},
 */
public final class LivingRenderContext extends AccessoryRenderContext<LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {
    public LivingRenderContext(PoseStack poseStack, SubmitNodeCollectorWrapper collector, ItemStack stack, LivingEntityRenderState state, EntityModel<LivingEntityRenderState> parentModel, ModelManager modelManager, int packedLight) {
        super(poseStack, collector, stack, state, parentModel, modelManager, packedLight);
    }
}
