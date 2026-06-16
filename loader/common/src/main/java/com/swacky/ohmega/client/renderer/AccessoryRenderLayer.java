package com.swacky.ohmega.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.client.renderer.HumanoidRenderContext;
import com.swacky.ohmega.api.client.renderer.IHumanoidAccessoryRenderer;
import com.swacky.ohmega.api.client.renderer.ILivingAccessoryRenderer;
import com.swacky.ohmega.api.client.renderer.LivingRenderContext;
import com.swacky.ohmega.api.client.renderer.SubmitNodeCollectorWrapper;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

// todo: cache renderer entries if possible
public final class AccessoryRenderLayer<T extends LivingEntityRenderState, U extends EntityModel<? super T>> extends RenderLayer<T, U> {
    private final EntityRendererProvider.Context providerContext;

    public AccessoryRenderLayer(EntityRendererProvider.Context providerContext, RenderLayerParent<T, U> renderer) {
        super(renderer);

        this.providerContext = providerContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, int packedLight, @NonNull T state, float yRot, float xRot) {
        AccessoryRenderStateData data = AccessoryRenderStateData.getData(state);

        if (data != null && !OhmegaHooks.renderPre(state, poseStack)) {
            SubmitNodeCollectorWrapper wrapper = new SubmitNodeCollectorWrapper(collector);
            NonNullList<ItemStack> stacks = data.stacks();
            boolean flag = OhmegaConfig.Server.isLoaded() && OhmegaConfig.Server.getData().allowHideAccessories().get();

            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);

                if (!stack.isEmpty() && !(flag && data.hidden()[i])) {
                    Accessory accessory = Accessories.get(stack.getItem());

                    if (state instanceof HumanoidRenderState humanoidState) {
                        IHumanoidAccessoryRenderer.Factory factory = AccessoryRenderers.getHumanoidFactory(accessory);

                        if (factory != null) {
                            HumanoidRenderContext context = new HumanoidRenderContext(
                                    poseStack,
                                    wrapper,
                                    stack,
                                    humanoidState,
                                    (HumanoidModel<HumanoidRenderState>) getParentModel(),
                                    Minecraft.getInstance().getModelManager(),
                                    packedLight);

                            if (!OhmegaHooks.renderAccessoryPre(context)) {
                                factory.construct(providerContext).submit(context);
                                OhmegaHooks.renderAccessoryPost(context);
                                continue;
                            }
                        }
                    }

                    ILivingAccessoryRenderer.Factory factory = AccessoryRenderers.getLivingFactory(accessory);

                    if (factory != null) {
                        LivingRenderContext context = new LivingRenderContext(
                                poseStack,
                                wrapper,
                                stack,
                                state,
                                (EntityModel<LivingEntityRenderState>) getParentModel(),
                                Minecraft.getInstance().getModelManager(),
                                packedLight);

                        if (!OhmegaHooks.renderAccessoryPre(context)) {
                            factory.construct(providerContext).submit(context);
                            OhmegaHooks.renderAccessoryPost(context);
                        }
                    }
                }
            }
        }
    }
}
