package com.swacky.ohmega.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.client.renderer.HumanoidRenderContext;
import com.swacky.ohmega.api.client.renderer.IHumanoidAccessoryRenderer;
import com.swacky.ohmega.api.client.renderer.ILivingAccessoryRenderer;
import com.swacky.ohmega.api.client.renderer.LivingRenderContext;
import com.swacky.ohmega.api.client.renderer.SubmitNodeCollectorWrapper;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

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
            ArrayList<AccessoryDataEntry> entries = data.entries();
            boolean flag = OhmegaConfig.Server.isLoaded() && OhmegaConfig.Server.getData().allowHideAccessories().get();

            for (AccessoryDataEntry entry : entries) {
                ItemStack stack = entry.getStack();

                if (!stack.isEmpty() && !(flag && entry.isHidden())) {
                    Item item = stack.getItem();

                    if (state instanceof HumanoidRenderState humanoidState) {
                        IHumanoidAccessoryRenderer.Factory factory = AccessoryRenderers.getHumanoidFactory(item);

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

                    ILivingAccessoryRenderer.Factory factory = AccessoryRenderers.getLivingFactory(item);

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
