package com.swacky.ohmega.compat.figura.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.event.AccessoryLayerRenderEvent;
import com.swacky.ohmega.api.client.event.AccessoryRenderEvent;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.compat.figura.client.api.event.OhmegaFiguraClientEventsAPI;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.world.ItemStackAPI;

public final class OhmegaFiguraClientEvents {
    private static boolean bootstrapped = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            AccessoryLayerRenderEvent.EVENT.register(OhmegaFiguraClientEvents::onAccessoryLayerRender);
            AccessoryRenderEvent.Post.EVENT.register(OhmegaFiguraClientEvents::onAccessoryRenderPost);
            AccessoryRenderEvent.Pre.EVENT.register(OhmegaFiguraClientEvents::onAccessoryRenderPre);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + OhmegaFiguraClientEvents.class + " multiple times");
        }
    }

    private static boolean onAccessoryLayerRender(LivingEntityRenderState state, PoseStack stack) {
        Avatar avatar = AvatarManager.getAvatar(state);

        if (avatar != null) {
            return OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_LAYER_RENDER_EVENT,
                    new Avatar.Instructions(Integer.MAX_VALUE)));
        }

        return false;
    }

    private static void onAccessoryRenderPost(AccessoryRenderContext<?, ?> context) {
        Avatar avatar = AvatarManager.getAvatar(context.state);

        if (avatar != null) {
           avatar.run(
                   OhmegaFiguraClientEventsAPI.ACCESSORY_RENDER_POST_EVENT,
                   new Avatar.Instructions(Integer.MAX_VALUE),
                   ItemStackAPI.verify(context.stack));
        }
    }

    private static boolean onAccessoryRenderPre(AccessoryRenderContext<?, ?> context) {
        Avatar avatar = AvatarManager.getAvatar(context.state);

        if (avatar != null) {
            return OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_LAYER_RENDER_EVENT,
                    new Avatar.Instructions(Integer.MAX_VALUE),
                    ItemStackAPI.verify(context.stack)));
        }

        return false;
    }
}
