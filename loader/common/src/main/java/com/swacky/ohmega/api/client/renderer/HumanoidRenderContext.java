package com.swacky.ohmega.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * An extension of the default render context, provided with {@link IHumanoidAccessoryRenderer},
 * providing some useful methods pertaining to entities with humanoid models
 */
public final class HumanoidRenderContext extends AccessoryRenderContext<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    public HumanoidRenderContext(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollectorWrapper collector, @NonNull ItemStack stack, @NonNull HumanoidRenderState state, @NonNull HumanoidModel<HumanoidRenderState> parentModel, @NonNull ModelManager modelManager, int packedLight) {
        super(poseStack, collector, stack, state, parentModel, modelManager, packedLight);
    }
}
