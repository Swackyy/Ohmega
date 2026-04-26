package com.swacky.ohmega.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.client.renderer.SubmitNodeCollectorWrapper;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public final class AccessoryRenderLayer<T extends LivingEntityRenderState, U extends EntityModel<? super T>> extends RenderLayer<T, U> {
    private final EntityRendererProvider.Context context;

    public AccessoryRenderLayer(EntityRendererProvider.Context context, RenderLayerParent<T, U> renderer) {
        super(renderer);
        this.context = context;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, int packedLight, @NonNull T state, float yRot, float xRot) {
        AccessoryRenderStateData data = AccessoryRenderStateData.getData(state);

        if (data != null && !OhmegaHooks.renderPre(state, poseStack)) {
            SubmitNodeCollectorWrapper wrapper = new SubmitNodeCollectorWrapper(collector);

            for (ItemStack stack : data.stacks()) {
                AccessoryRenderers.RendererFactory factory = AccessoryRenderers.getFactoryFor(Accessories.get(stack.getItem()));

                if (factory != null) {
                    AccessoryRenderContext context = new AccessoryRenderContext(
                            poseStack,
                            wrapper,
                            stack,
                            state,
                            getParentModel(),
                            Minecraft.getInstance().getModelManager(),
                            packedLight);

                    if (!OhmegaHooks.renderItemPre(context)) {
                        factory.create(this.context).submit(context);
                        OhmegaHooks.renderItemPost(context);
                    }
                }
            }
        }
    }
}
