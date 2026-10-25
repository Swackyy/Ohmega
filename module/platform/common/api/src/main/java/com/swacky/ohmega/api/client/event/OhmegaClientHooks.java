package com.swacky.ohmega.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.OhmegaClient;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jspecify.annotations.NonNull;

public class OhmegaClientHooks {
    private static final Service IMPL = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static void extensionExtractPost(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        IMPL.extensionExtractPost(gui, extension);
    }

    public static boolean extensionExtractPre(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        return IMPL.extensionExtractPre(gui, extension);
    }

    public static void extensionInitPost(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder) {
        IMPL.extensionInitPost(extension, adder);
    }

    public static boolean extensionInitPre(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder) {
        return IMPL.extensionInitPre(extension, adder);
    }

    public static boolean renderAccessoryLayer(LivingEntityRenderState state, PoseStack stack) {
        return IMPL.renderAccessoryLayer(state, stack);
    }

    public static void renderAccessoryPost(AccessoryRenderContext<?, ?> context) {
        IMPL.renderAccessoryPost(context);
    }

    public static boolean renderAccessoryPre(AccessoryRenderContext<?, ?> context) {
        return IMPL.renderAccessoryPre(context);
    }

    public interface Service {
        void extensionExtractPost(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension);

        boolean extensionExtractPre(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension);

        void extensionInitPost(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder);

        boolean extensionInitPre(AccessoryScreenExtension extension, AccessoryScreenExtension.WidgetAdder adder);

        boolean renderAccessoryLayer(LivingEntityRenderState state, PoseStack stack);

        void renderAccessoryPost(AccessoryRenderContext<?, ?> context);

        boolean renderAccessoryPre(AccessoryRenderContext<?, ?> context);
    }
}
