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
    public void extensionInitPost(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder) {
        NeoForge.EVENT_BUS.post(new AccessoryExtensionInitEvent.Post(extension, adder));
    }

    @Override
    public boolean extensionInitPre(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder) {
        return NeoForge.EVENT_BUS.post(new AccessoryExtensionInitEvent.Pre(extension, adder)).isCanceled();
    }

    @Override
    public boolean renderAccessoryLayer(LivingEntityRenderState state, PoseStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryLayerRenderEvent(state, stack)).isCanceled();
    }

    @Override
    public void renderAccessoryPost(AccessoryRenderContext<?, ?> context) {
        NeoForge.EVENT_BUS.post(new AccessoryRenderEvent.Post(context));
    }

    @Override
    public boolean renderAccessoryPre(AccessoryRenderContext<?, ?> context) {
        return NeoForge.EVENT_BUS.post(new AccessoryRenderEvent.Pre(context)).isCanceled();
    }
}
