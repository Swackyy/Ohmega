package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateDataImpl;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class Fabric_LivingEntityRendererMixin<T extends LivingEntity, U extends LivingEntityRenderState, V extends EntityModel<? super U>> extends EntityRenderer<T, U> implements RenderLayerParent<U, V> {
    protected Fabric_LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At(
                    value = "TAIL"))
    public void extractRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        // todo:
        // todo 2: make this (and others) a client callback
        if (entity instanceof AbstractClientPlayer player) {
            AccessoryContainer container = AccessoryHelper.getContainer(player);
            NonNullList<ItemStack> stacks = container.getStacks();
            NonNullList<ItemStack> stacksFiltered = NonNullList.createWithCapacity(stacks.size());

            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = container.getStackInSlot(i);

                if (!stack.isEmpty() && container.isVisible(i)) {
                    stacksFiltered.add(stack);
                }
            }

            state.setData(AccessoryRenderStateDataImpl.KEY, new AccessoryRenderStateData(stacksFiltered));
        }
    }
}
