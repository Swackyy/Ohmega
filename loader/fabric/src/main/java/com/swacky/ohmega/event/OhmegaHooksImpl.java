package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderContext;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.api.event.AccessoryAllowWalkOnPowderSnowEvent;
import com.swacky.ohmega.api.event.AccessoryAutoSyncEvent;
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
import com.swacky.ohmega.api.event.RegisterAccessoryTypesEvent;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.IdentityHashMap;
import java.util.Map;

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
    public boolean keybindUse(Player player, ItemStack stack) {
        return AccessoryUseEvent.EVENT.invoker().process(player, stack);
    }

    @Override
    public double mobVisibility(ItemStack stack, Entity targetingEntity, double original) {
        return AccessoryMobVisibilityEvent.EVENT.invoker().process(stack, targetingEntity, original);
    }

    @Override
    public Map<Item, Pair<AccessoryType, Boolean>> overrideTypes() {
        Map<Item, Pair<AccessoryType, Boolean>> map = new IdentityHashMap<>();

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
    public void renderAccessoryPost(AccessoryRenderContext<?, ?> context) {
        AccessoryRenderEvent.Post.EVENT.invoker().process(context);
    }

    @Override
    public boolean renderAccessoryPre(AccessoryRenderContext<?, ?> context) {
        return AccessoryRenderEvent.Pre.EVENT.invoker().process(context);
    }

    @Override
    public boolean renderPre(LivingEntityRenderState state, PoseStack stack) {
        return AccessoryRenderPreEvent.EVENT.invoker().process(state, stack);
    }

    @Override
    public boolean unequip(LivingEntity entity, ItemStack stack) {
        return AccessoryUnequipEvent.EVENT.invoker().process(entity, stack);
    }
}
