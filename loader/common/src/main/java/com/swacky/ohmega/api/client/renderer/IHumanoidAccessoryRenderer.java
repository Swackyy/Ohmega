package com.swacky.ohmega.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jspecify.annotations.NonNull;

/**
 * Accessory renderer interface for humanoid entities
 * <p>
 * To allow for {@link ModelPart} translations to function properly, the {@link PoseStack} is translated by 24 pixels up automatically for humanoids
 */
public interface IHumanoidAccessoryRenderer extends IAccessoryRenderer<HumanoidRenderContext> {
    interface Factory {
        @NonNull IHumanoidAccessoryRenderer construct(EntityRendererProvider.@NonNull Context providerContext);
    }
}
