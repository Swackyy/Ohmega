package com.swacky.ohmega.common.event;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
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
import com.swacky.ohmega.api.common.event.AccessoryTickEvent;
import com.swacky.ohmega.api.common.event.AccessoryUnequipEvent;
import com.swacky.ohmega.api.common.event.AccessoryUseEvent;
import com.swacky.ohmega.api.common.event.OhmegaHooks;
import com.swacky.ohmega.api.common.event.RegisterAccessoryTypesEvent;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.api.client.event.AccessoryExtensionRenderEvent;
import com.swacky.ohmega.api.client.event.AccessoryRenderEvent;
import com.swacky.ohmega.api.client.event.AccessoryLayerRenderEvent;
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
import org.jspecify.annotations.NonNull;

import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryBind() {
        AccessoryBindEvent.EVENT.invoker().process();
    }

    @Override
    public void accessoryTickPost(LivingEntity entity, ItemStack stack) {
        AccessoryTickEvent.Post.EVENT.invoker().process(entity, stack);
    }

    @Override
    public boolean accessoryTickPre(LivingEntity entity, ItemStack stack) {
        return AccessoryTickEvent.Pre.EVENT.invoker().process(entity, stack);
    }

    @Override
    public boolean allowWalkOnPowderSnow(ItemStack stack, boolean original) {
        return AccessoryAllowWalkOnPowderSnowEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public boolean autoSync(ItemStack stack, boolean original) {
        return AccessoryAutoSyncEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public byte autoSyncModulo(ItemStack stack, byte original) {
        return AccessoryAutoSyncModuloEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public boolean canEquip(LivingEntity entity, ItemStack stack, EquipContext context, boolean original) {
        return AccessoryCanEquipEvent.EVENT.invoker().process(entity, stack, context, original);
    }

    @Override
    public boolean canUnequip(LivingEntity entity, ItemStack stack, boolean original) {
        return AccessoryCanUnequipEvent.EVENT.invoker().process(entity, stack, original);
    }

    @Override
    public boolean compatibleWith(ItemStack stack, ItemStack other, boolean original) {
        return AccessoryCompatibleWithEvent.EVENT.invoker().process(stack, other, original);
    }

    @Override
    public boolean equip(LivingEntity entity, ItemStack stack, EquipContext context) {
        return AccessoryEquipEvent.EVENT.invoker().process(entity, stack, context);
    }

    @Override
    public SoundData equipSound(ItemStack stack, SoundData original) {
        return AccessoryEquipSoundEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public boolean isPiglinSafe(ItemStack stack, boolean original) {
        return AccessoryIsPiglinSafeEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public BooleanBooleanPair keybindUse(Player player, ItemStack stack) {
        return AccessoryUseEvent.EVENT.invoker().process(player, stack);
    }

    @Override
    public double mobVisibility(ItemStack stack, Entity targetingEntity, double original) {
        return AccessoryMobVisibilityEvent.EVENT.invoker().process(stack, targetingEntity, original);
    }

    @Override
    public Map<Item, BooleanObjectPair<AccessoryType>> overrideTypes() {
        Map<Item, BooleanObjectPair<AccessoryType>> map = new IdentityHashMap<>();

        AccessoryOverrideTypesEvent.EVENT.invoker().process(map);
        return map;
    }

    @Override
    public boolean preferInventoryTick(ItemStack stack, boolean original) {
        return AccessoryPreferInventoryTickEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public boolean preferVanillaUse(ItemStack stack, boolean original) {
        return AccessoryPreferVanillaUseEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public Map<Identifier, AccessoryType> registerAccessoryTypes() {
        ImmutableMap.Builder<Identifier, AccessoryType> builder = new ImmutableMap.Builder<>();

        RegisterAccessoryTypesEvent.EVENT.invoker().process(builder);
        return builder.build();
    }

    @Override
    public void renderAccessoryExtensionPost(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        AccessoryExtensionRenderEvent.Post.EVENT.invoker().process(gui, extension);
    }

    @Override
    public boolean renderAccessoryExtensionPre(@NonNull GuiGraphicsExtractor gui, @NonNull AccessoryScreenExtension extension) {
        return AccessoryExtensionRenderEvent.Pre.EVENT.invoker().process(gui, extension);
    }

    @Override
    public boolean renderAccessoryLayer(LivingEntityRenderState state, PoseStack stack) {
        return AccessoryLayerRenderEvent.EVENT.invoker().process(state, stack);
    }

    @Override
    public void renderAccessoryPost(AccessoryRenderContext<?, ?> context) {
        AccessoryRenderEvent.Post.EVENT.invoker().process(context);
    }

    @Override
    public boolean renderAccessoryPre(AccessoryRenderContext<?, ?> context) {
        return AccessoryRenderEvent.Pre.EVENT.invoker().process(context);
    }

    @Override
    public boolean unequip(LivingEntity entity, ItemStack stack, EquipContext context) {
        return AccessoryUnequipEvent.EVENT.invoker().process(entity, stack, context);
    }
}
