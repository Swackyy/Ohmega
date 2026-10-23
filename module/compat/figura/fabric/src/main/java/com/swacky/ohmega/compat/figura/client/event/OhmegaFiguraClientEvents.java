package com.swacky.ohmega.compat.figura.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.event.AccessoryLayerRenderEvent;
import com.swacky.ohmega.api.client.event.AccessoryRenderEvent;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.common.event.AccessoryEquipEvent;
import com.swacky.ohmega.api.common.event.AccessoryTickEvent;
import com.swacky.ohmega.api.common.event.AccessoryUnequipEvent;
import com.swacky.ohmega.api.common.event.AccessoryUseEvent;
import com.swacky.ohmega.api.common.item.AccessoryContext;
import com.swacky.ohmega.compat.figura.client.api.event.OhmegaFiguraClientEventsAPI;
import com.swacky.ohmega.compat.figura.client.api.item.AccessoryContextAPI;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanPair;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.entity.LivingEntityAPI;
import org.figuramc.figura.lua.api.world.ItemStackAPI;

public final class OhmegaFiguraClientEvents {
    private static boolean bootstrapped = false;

    private static final Avatar.Instructions ACCESSORY_LAYER_INST = new Avatar.Instructions(Integer.MAX_VALUE);
    private static final Avatar.Instructions ACCESSORY_RENDER_INST = new Avatar.Instructions(Integer.MAX_VALUE);
    private static final Avatar.Instructions ACCESSORY_EQUIP_INST = new Avatar.Instructions(Integer.MAX_VALUE);
    private static final Avatar.Instructions ACCESSORY_TICK_INST = new Avatar.Instructions(Integer.MAX_VALUE);
    private static final Avatar.Instructions ACCESSORY_UNEQUIP_INST = new Avatar.Instructions(Integer.MAX_VALUE);
    private static final Avatar.Instructions ACCESSORY_USE_INST = new Avatar.Instructions(Integer.MAX_VALUE);

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            // Client
            AccessoryLayerRenderEvent.EVENT.register(OhmegaFiguraClientEvents::onAccessoryLayerRender);
            AccessoryRenderEvent.Post.EVENT.register(OhmegaFiguraClientEvents::onAccessoryRenderPost);
            AccessoryRenderEvent.Pre.EVENT.register(OhmegaFiguraClientEvents::onAccessoryRenderPre);

            // Common (in spirit, just to keep the ordering)
            AccessoryEquipEvent.EVENT.register(OhmegaFiguraClientEvents::onAccessoryEquip);
            AccessoryTickEvent.Post.EVENT.register(OhmegaFiguraClientEvents::onAccessoryTickPost);
            AccessoryTickEvent.Pre.EVENT.register(OhmegaFiguraClientEvents::onAccessoryTickPre);
            AccessoryUnequipEvent.EVENT.register(OhmegaFiguraClientEvents::onAccessoryUnequip);
            AccessoryUseEvent.EVENT.register(OhmegaFiguraClientEvents::onAccessoryUse);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + OhmegaFiguraClientEvents.class + " multiple times");
        }
    }

    private static boolean onAccessoryLayerRender(LivingEntityRenderState state, PoseStack stack) {
        Avatar avatar = AvatarManager.getAvatar(state);

        if (avatar != null) {
            return OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_LAYER_RENDER,
                    ACCESSORY_LAYER_INST));
        }

        return false;
    }

    private static void onAccessoryRenderPost(AccessoryRenderContext<?, ?> context) {
        Avatar avatar = AvatarManager.getAvatar(context.state);

        if (avatar != null) {
            avatar.run(
                   OhmegaFiguraClientEventsAPI.ACCESSORY_RENDER_POST,
                   ACCESSORY_RENDER_INST.post(),
                   ItemStackAPI.verify(context.stack));
        }
    }

    private static boolean onAccessoryRenderPre(AccessoryRenderContext<?, ?> context) {
        Avatar avatar = AvatarManager.getAvatar(context.state);

        if (avatar != null) {
            return OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_RENDER_PRE,
                    ACCESSORY_RENDER_INST,
                    ItemStackAPI.verify(context.stack)));
        }

        return false;
    }

    private static boolean onAccessoryEquip(LivingEntity entity, ItemStack stack, AccessoryContext context) {
        Avatar avatar = AvatarManager.getAvatar(entity);

        if (avatar != null) {
            return OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_EQUIP,
                    ACCESSORY_EQUIP_INST,
                    LivingEntityAPI.wrap(entity),
                    ItemStackAPI.verify(stack),
                    new AccessoryContextAPI(context)));
        }

        return false;
    }

    private static void onAccessoryTickPost(LivingEntity entity, ItemStack stack) {
        Avatar avatar = AvatarManager.getAvatar(entity);

        if (avatar != null) {
            avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_TICK_POST,
                    ACCESSORY_TICK_INST.post(),
                    LivingEntityAPI.wrap(entity),
                    ItemStackAPI.verify(stack));
        }
    }

    private static boolean onAccessoryTickPre(LivingEntity entity, ItemStack stack) {
        Avatar avatar = AvatarManager.getAvatar(entity);

        if (avatar != null) {
            return OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_TICK_PRE,
                    ACCESSORY_TICK_INST,
                    LivingEntityAPI.wrap(entity),
                    ItemStackAPI.verify(stack)));
        }

        return false;
    }

    private static boolean onAccessoryUnequip(LivingEntity entity, ItemStack stack, AccessoryContext context) {
        Avatar avatar = AvatarManager.getAvatar(entity);

        if (avatar != null) {
            return OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_UNEQUIP,
                    ACCESSORY_UNEQUIP_INST,
                    LivingEntityAPI.wrap(entity),
                    ItemStackAPI.verify(stack),
                    new AccessoryContextAPI(context)));
        }

        return false;
    }

    private static BooleanBooleanPair onAccessoryUse(LivingEntity entity, ItemStack stack) {
        Avatar avatar = AvatarManager.getAvatar(entity);

        if (avatar != null) {
            return BooleanBooleanPair.of(OhmegaFiguraClientEventsAPI.isCancelled(avatar.run(
                    OhmegaFiguraClientEventsAPI.ACCESSORY_USE,
                    ACCESSORY_USE_INST,
                    LivingEntityAPI.wrap(entity),
                    ItemStackAPI.verify(stack))
            ), false);
        }

        return BooleanBooleanPair.of(false, false);
    }
}
