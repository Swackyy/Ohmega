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
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class OhmegaClientHooksImpl implements OhmegaClientHooks.Service {
    @Override
    public void extensionExtractPost(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        NeoForge.EVENT_BUS.post(new AccessoryExtensionExtractEvent.Post(gui, extension));
    }

    @Override
    public boolean extensionExtractPre(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        return NeoForge.EVENT_BUS.post(new AccessoryExtensionExtractEvent.Pre(gui, extension)).isCanceled();
    }

    @Override
    public void extensionInitPost(@NonNull AccessoryScreenExtension extension, AccessoryScreenExtension.@NonNull WidgetAdder adder) {
        NeoForge.EVENT_BUS.post(new AccessoryExtensionInitEvent.Post(extension, adder));
    }

    @Override
    public boolean extensionInitPre(@NonNull AccessoryScreenExtension extension, AccessoryScreenExtension.@NonNull WidgetAdder adder) {
        return NeoForge.EVENT_BUS.post(new AccessoryExtensionInitEvent.Pre(extension, adder)).isCanceled();
    }

    @Override
    public boolean renderAccessoryLayer(@NonNull LivingEntityRenderState state, PoseStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryLayerRenderEvent(state, stack)).isCanceled();
    }

    @Override
    public void renderAccessoryPost(@NonNull AccessoryRenderContext<?, ?> context) {
        NeoForge.EVENT_BUS.post(new AccessoryRenderEvent.Post(context));
    }

    @Override
    public boolean renderAccessoryPre(@NonNull AccessoryRenderContext<?, ?> context) {
        return NeoForge.EVENT_BUS.post(new AccessoryRenderEvent.Pre(context)).isCanceled();
    }
}
