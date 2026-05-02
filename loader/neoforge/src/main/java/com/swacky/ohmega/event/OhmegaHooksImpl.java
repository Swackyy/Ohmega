package com.swacky.ohmega.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.common.item.AccessoryModifiers;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.event.AccessoryAllowWalkOnPowderSnowEvent;
import com.swacky.ohmega.api.event.AccessoryAttributeModifiersEvent;
import com.swacky.ohmega.api.event.AccessoryAutoSyncEventEvent;
import com.swacky.ohmega.api.event.AccessoryBindEvent;
import com.swacky.ohmega.api.event.AccessoryCanEquipEvent;
import com.swacky.ohmega.api.event.AccessoryCanUnequipEvent;
import com.swacky.ohmega.api.event.AccessoryCompatibleWithEvent;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.api.event.AccessoryEquipSoundEvent;
import com.swacky.ohmega.api.event.AccessoryIsPiglinSafeEvent;
import com.swacky.ohmega.api.event.AccessoryMobVisibilityEvent;
import com.swacky.ohmega.api.event.AccessoryOverrideTypesEvent;
import com.swacky.ohmega.api.event.AccessoryPreferInventoryTickEvent;
import com.swacky.ohmega.api.event.AccessoryPreferVanillaUseEvent;
import com.swacky.ohmega.api.event.AccessoryRenderEvent;
import com.swacky.ohmega.api.event.AccessoryRenderPreEvent;
import com.swacky.ohmega.api.event.AccessoryTickEvent;
import com.swacky.ohmega.api.event.AccessoryUnequipEvent;
import com.swacky.ohmega.api.event.AccessoryUseEvent;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.event.RegisterAccessoryTypesEvent;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
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
        NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Post(entity, stack));
    }

    @Override
    public boolean accessoryTickPre(LivingEntity entity, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Pre(entity, stack)).isCanceled();
    }

    @Override
    public boolean allowWalkOnPowderSnow(ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryAllowWalkOnPowderSnowEvent(stack, original)).returnValue;
    }

    @Override
    public AccessoryModifiers attributeModifiers(ItemStack stack, AccessoryModifiers.Builder builder) {
        NeoForge.EVENT_BUS.post(new AccessoryAttributeModifiersEvent(stack, builder));
        return builder.build();
    }

    @Override
    public boolean autoSync(ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryAutoSyncEventEvent(stack, original)).returnValue;
    }

    @Override
    public boolean canEquip(LivingEntity entity, ItemStack stack, EquipContext context, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanEquipEvent(entity, stack, context, original)).returnValue;
    }

    @Override
    public boolean canUnequip(LivingEntity entity, ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanUnequipEvent(entity, stack, original)).returnValue;
    }

    @Override
    public boolean compatibleWith(ItemStack stack, ItemStack other, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryCompatibleWithEvent(stack, other, original)).returnValue;
    }

    @Override
    public boolean equip(LivingEntity entity, ItemStack stack, EquipContext context) {
        return NeoForge.EVENT_BUS.post(new AccessoryEquipEvent(entity, stack, context)).isCanceled();
    }

    @Override
    public SoundData equipSound(ItemStack stack, SoundData original) {
        return NeoForge.EVENT_BUS.post(new AccessoryEquipSoundEvent(stack, original)).returnValue;
    }

    @Override
    public boolean isPiglinSafe(ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryIsPiglinSafeEvent(stack, original)).returnValue;
    }

    @Override
    public boolean keybindUse(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryUseEvent(player, stack)).isCanceled();
    }

    @Override
    public double mobVisibility(ItemStack stack, Entity targetingEntity, double original) {
        return NeoForge.EVENT_BUS.post(new AccessoryMobVisibilityEvent(stack, targetingEntity, original)).returnValue;
    }

    @Override
    public Map<Item, Pair<AccessoryType, Boolean>> overrideTypes() {
        Map<Item, Pair<AccessoryType, Boolean>> map = new IdentityHashMap<>();

        ModLoader.postEvent(new AccessoryOverrideTypesEvent(map));
        return map;
    }

    @Override
    public boolean preferInventoryTick(ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryPreferInventoryTickEvent(stack, original)).returnValue;
    }

    @Override
    public boolean preferVanillaUse(ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryPreferVanillaUseEvent(stack, original)).returnValue;
    }

    @Override
    public Map<Identifier, AccessoryType> registerAccessoryTypes() {
        Map<Identifier, AccessoryType> map = new HashMap<>();

        NeoForge.EVENT_BUS.post(new RegisterAccessoryTypesEvent(map));
        return map;
    }

    @Override
    public void renderAccessoryPost(AccessoryRenderContext<?, ?> context) {
        NeoForge.EVENT_BUS.post(new AccessoryRenderEvent.Post(context));
    }

    @Override
    public boolean renderAccessoryPre(AccessoryRenderContext<?, ?> context) {
        return NeoForge.EVENT_BUS.post(new AccessoryRenderEvent.Pre(context)).isCanceled();
    }

    @Override
    public boolean renderPre(LivingEntityRenderState state, PoseStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryRenderPreEvent(state, stack)).isCanceled();
    }

    @Override
    public boolean unequip(LivingEntity entity, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryUnequipEvent(entity, stack)).isCanceled();
    }
}
