package com.swacky.ohmega.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.event.AccessoryExtensionExtractEvent;
import com.swacky.ohmega.api.client.event.AccessoryExtensionInitEvent;
import com.swacky.ohmega.api.client.event.AccessoryLayerRenderEvent;
import com.swacky.ohmega.api.client.event.AccessoryRenderEvent;
import com.swacky.ohmega.api.client.event.OhmegaClientHooks;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class OhmegaClientHooksImpl implements OhmegaClientHooks.Service {
    @Override
    public void extensionExtractPost(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        AccessoryExtensionExtractEvent.Post.EVENT.invoker().process(gui, extension);
    }

    @Override
    public boolean extensionExtractPre(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        return AccessoryExtensionExtractEvent.Pre.EVENT.invoker().process(gui, extension);
    }

    @Override
    public void extensionInitPost(@NonNull AccessoryScreenExtension extension, AccessoryScreenExtension.@NonNull WidgetAdder adder) {
        AccessoryExtensionInitEvent.Post.EVENT.invoker().process(extension, adder);
    }

    @Override
    public boolean extensionInitPre(@NonNull AccessoryScreenExtension extension, AccessoryScreenExtension.@NonNull WidgetAdder adder) {
        return AccessoryExtensionInitEvent.Pre.EVENT.invoker().process(extension, adder);
    }

    @Override
    public boolean renderAccessoryLayer(@NonNull LivingEntityRenderState state, PoseStack stack) {
        return AccessoryLayerRenderEvent.EVENT.invoker().process(state, stack);
    }

    @Override
    public void renderAccessoryPost(@NonNull AccessoryRenderContext<?, ?> context) {
        AccessoryRenderEvent.Post.EVENT.invoker().process(context);
    }

    @Override
    public boolean renderAccessoryPre(@NonNull AccessoryRenderContext<?, ?> context) {
        return AccessoryRenderEvent.Pre.EVENT.invoker().process(context);
    }
}
