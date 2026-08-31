package com.swacky.ohmega.common.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.event.AccessoryExtensionRenderEvent;
import com.swacky.ohmega.api.client.event.AccessoryLayerRenderEvent;
import com.swacky.ohmega.api.client.event.AccessoryRenderEvent;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.event.AccessoryAllowWalkOnPowderSnowEvent;
import com.swacky.ohmega.api.common.event.AccessoryAutoSyncEvent;
import com.swacky.ohmega.api.common.event.AccessoryAutoSyncModuloEvent;
import com.swacky.ohmega.api.common.event.AccessoryBindEvent;
import com.swacky.ohmega.api.common.event.AccessoryCanEquipEvent;
import com.swacky.ohmega.api.common.event.AccessoryCanUnequipEvent;
import com.swacky.ohmega.api.common.event.AccessoryCompatibleWithEvent;
import com.swacky.ohmega.api.common.event.AccessoryEquipEvent;
import com.swacky.ohmega.api.common.event.AccessoryEquipSoundEvent;
import com.swacky.ohmega.api.common.event.AccessoryIsPiglinSafeEvent;
import com.swacky.ohmega.api.common.event.AccessoryMobVisibilityEvent;
import com.swacky.ohmega.api.common.event.AccessoryOverrideTypesEvent;
import com.swacky.ohmega.api.common.event.AccessoryPreferInventoryTickEvent;
import com.swacky.ohmega.api.common.event.AccessoryPreferVanillaUseEvent;
import com.swacky.ohmega.api.common.event.AccessoryShouldDropOnDeathEvent;
import com.swacky.ohmega.api.common.event.AccessoryTickEvent;
import com.swacky.ohmega.api.common.event.AccessoryUnequipEvent;
import com.swacky.ohmega.api.common.event.AccessoryUseEvent;
import com.swacky.ohmega.api.common.event.OhmegaHooks;
import com.swacky.ohmega.api.common.event.RegisterAccessoryTypesEvent;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.SoundData;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanPair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("unused")
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
    public boolean autoSync(ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryAutoSyncEvent(stack, original)).returnValue;
    }

    @Override
    public byte autoSyncModulo(ItemStack stack, byte original) {
        return NeoForge.EVENT_BUS.post(new AccessoryAutoSyncModuloEvent(stack, original)).returnValue;

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
    public BooleanBooleanPair keybindUse(Player player, ItemStack stack) {
        MutableBoolean shouldSynchronise = new MutableBoolean(false);

        return BooleanBooleanPair.of(NeoForge.EVENT_BUS.post(new AccessoryUseEvent(player, stack, shouldSynchronise)).isCanceled(), shouldSynchronise.booleanValue());
    }

    @Override
    public double mobVisibility(ItemStack stack, Entity targetingEntity, double original) {
        return NeoForge.EVENT_BUS.post(new AccessoryMobVisibilityEvent(stack, targetingEntity, original)).returnValue;
    }

    @Override
    public Map<Item, BooleanObjectPair<AccessoryType>> overrideTypes() {
        Map<Item, BooleanObjectPair<AccessoryType>> map = new IdentityHashMap<>();

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
    public void renderAccessoryExtensionPost(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        NeoForge.EVENT_BUS.post(new AccessoryExtensionRenderEvent.Post(gui, extension));
    }

    @Override
    public boolean renderAccessoryExtensionPre(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        return NeoForge.EVENT_BUS.post(new AccessoryExtensionRenderEvent.Pre(gui, extension)).isCanceled();
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

    @Override
    public boolean shouldDropOnDeath(@NonNull ItemStack stack, @NonNull LivingEntity entity, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryShouldDropOnDeathEvent(stack, entity, original)).returnValue;
    }

    @Override
    public boolean unequip(LivingEntity entity, ItemStack stack, EquipContext context) {
        return NeoForge.EVENT_BUS.post(new AccessoryUnequipEvent(entity, stack, context)).isCanceled();
    }
}
