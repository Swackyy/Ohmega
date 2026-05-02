package com.swacky.ohmega.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.common.item.AccessoryModifiers;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryBind() {
        ModLoader.postEvent(new AccessoryBindEvent());
    }

    @Override
    public void accessoryTickPost(LivingEntity entity, ItemStack stack) {
        AccessoryTickEvent.Post.BUS.post(new AccessoryTickEvent.Post(entity, stack));
    }

    @Override
    public boolean accessoryTickPre(LivingEntity entity, ItemStack stack) {
        return AccessoryTickEvent.Pre.BUS.post(new AccessoryTickEvent.Pre(entity, stack));
    }

    @Override
    public boolean allowWalkOnPowderSnow(ItemStack stack, boolean original) {
        AccessoryAllowWalkOnPowderSnowEvent event = new AccessoryAllowWalkOnPowderSnowEvent(stack, original);

        AccessoryAllowWalkOnPowderSnowEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public AccessoryModifiers attributeModifiers(ItemStack stack, AccessoryModifiers.Builder builder) {
        AccessoryAttributeModifiersEvent event = new AccessoryAttributeModifiersEvent(stack, builder);

        AccessoryAttributeModifiersEvent.BUS.post(event);
        return builder.build();
    }

    @Override
    public boolean autoSync(ItemStack stack, boolean original) {
        AccessoryAutoSyncEvent event = new AccessoryAutoSyncEvent(stack, original);

        AccessoryAutoSyncEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean canEquip(LivingEntity entity, ItemStack stack, EquipContext context, boolean original) {
        AccessoryCanEquipEvent event = new AccessoryCanEquipEvent(entity, stack, context, original);

        AccessoryCanEquipEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean canUnequip(LivingEntity entity, ItemStack stack, boolean original) {
        AccessoryCanUnequipEvent event = new AccessoryCanUnequipEvent(entity, stack, original);

        AccessoryCanUnequipEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean compatibleWith(ItemStack stack, ItemStack other, boolean original) {
        AccessoryCompatibleWithEvent event = new AccessoryCompatibleWithEvent(stack, other, original);

        AccessoryCompatibleWithEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean equip(LivingEntity entity, ItemStack stack, EquipContext context) {
        return AccessoryEquipEvent.BUS.post(new AccessoryEquipEvent(entity, stack, context));
    }

    @Override
    public SoundData equipSound(ItemStack stack, SoundData original) {
        AccessoryEquipSoundEvent event = new AccessoryEquipSoundEvent(stack, original);

        AccessoryEquipSoundEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean isPiglinSafe(ItemStack stack, boolean original) {
        AccessoryIsPiglinSafeEvent event = new AccessoryIsPiglinSafeEvent(stack, original);

        AccessoryIsPiglinSafeEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean keybindUse(Player player, ItemStack stack) {
        return AccessoryUseEvent.BUS.post(new AccessoryUseEvent(player, stack));
    }

    @Override
    public double mobVisibility(ItemStack stack, Entity targetingEntity, double original) {
        AccessoryMobVisibilityEvent event = new AccessoryMobVisibilityEvent(stack, targetingEntity, original);

        AccessoryMobVisibilityEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public Map<Item, Pair<AccessoryType, Boolean>> overrideTypes() {
        Map<Item, Pair<AccessoryType, Boolean>> map = new IdentityHashMap<>();

        ModLoader.postEvent(new AccessoryOverrideTypesEvent(map));
        return map;
    }

    @Override
    public boolean preferInventoryTick(ItemStack stack, boolean original) {
        AccessoryPreferInventoryTickEvent event = new AccessoryPreferInventoryTickEvent(stack, original);

        AccessoryPreferInventoryTickEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean preferVanillaUse(ItemStack stack, boolean original) {
        AccessoryPreferVanillaUseEvent event = new AccessoryPreferVanillaUseEvent(stack, original);

        AccessoryPreferVanillaUseEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public Map<Identifier, AccessoryType> registerAccessoryTypes() {
        Map<Identifier, AccessoryType> map = new HashMap<>();

        RegisterAccessoryTypesEvent.BUS.post(new RegisterAccessoryTypesEvent(map));
        return map;
    }

    @Override
    public void renderAccessoryPost(AccessoryRenderContext<?, ?> context) {
        AccessoryRenderEvent.Post.BUS.post(new AccessoryRenderEvent.Post(context));
    }

    @Override
    public boolean renderAccessoryPre(AccessoryRenderContext<?, ?> context) {
        return AccessoryRenderEvent.Pre.BUS.post(new AccessoryRenderEvent.Pre(context));
    }

    @Override
    public boolean renderPre(LivingEntityRenderState state, PoseStack stack) {
        return AccessoryRenderPreEvent.BUS.post(new AccessoryRenderPreEvent(state, stack));
    }

    @Override
    public boolean unequip(LivingEntity entity, ItemStack stack) {
        return AccessoryUnequipEvent.BUS.post(new AccessoryUnequipEvent(entity, stack));
    }
}
