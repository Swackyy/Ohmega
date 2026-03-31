package com.swacky.ohmega.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.client.renderer.IAccessoryRenderer;
import com.swacky.ohmega.api.client.renderer.SubmitNodeCollectorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class AccessoryRenderLayer<T extends LivingEntityRenderState, U extends EntityModel<? super T>> extends RenderLayer<T, U> {
    private final EntityRendererProvider.Context context;

    public AccessoryRenderLayer(EntityRendererProvider.Context context, RenderLayerParent<T, U> renderer) {
        super(renderer);
        this.context = context;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, int packedLight, T state, float yRot, float xRot) {
        AccessoryRenderStateData data = AccessoryRenderStateData.getData(state);

        if (data != null) {
            SubmitNodeCollectorWrapper wrapper = new SubmitNodeCollectorWrapper(collector);

            for (ItemStack stack : data.stacks()) {
                Function<EntityRendererProvider.Context, IAccessoryRenderer> factory = AccessoryRenderers.getFactoryFor(stack.getItem());

                if (factory != null) {
                    factory.apply(context).submit(new AccessoryRenderContext(
                            poseStack,
                            wrapper,
                            stack,
                            state,
                            Minecraft.getInstance().getModelManager(),
                            packedLight));
                }
            }
        }
    }
}
