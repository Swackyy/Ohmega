package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.SoundData;
import com.swacky.ohmega.api.event.AccessoryAllowWalkOnPowderSnowEvent;
import com.swacky.ohmega.api.event.AccessoryAttributeModifiersEvent;
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
import com.swacky.ohmega.api.event.AccessoryPreferVanillaUseEvent;
import com.swacky.ohmega.api.event.AccessoryTickEvent;
import com.swacky.ohmega.api.event.AccessoryUnequipEvent;
import com.swacky.ohmega.api.event.AccessoryUseEvent;
import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.api.event.RegisterAccessoryTypesEvent;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryBind() {
        AccessoryBindEvent.EVENT.invoker().process();
    }

    @Override
    public void accessoryTickPost(Player player, ItemStack stack) {
        AccessoryTickEvent.Post.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean accessoryTickPre(Player player, ItemStack stack) {
        return AccessoryTickEvent.Pre.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean allowWalkOnPowderSnow(ItemStack stack, boolean original) {
        return AccessoryAllowWalkOnPowderSnowEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public AccessoryModifiers attributeModifiers(ItemStack stack, AccessoryModifiers.Builder builder) {
        AccessoryAttributeModifiersEvent.EVENT.invoker().process(stack, builder);
        return builder.build();
    }

    @Override
    public boolean autoSync(ItemStack stack, boolean original) {
        return AccessoryAutoSyncEvent.EVENT.invoker().process(stack, original);
    }

    @Override
    public boolean canEquip(Player player, ItemStack stack, EquipContext context, boolean original) {
        return AccessoryCanEquipEvent.EVENT.invoker().process(player, stack, context, original);
    }

    @Override
    public boolean canUnequip(Player player, ItemStack stack, boolean original) {
        return AccessoryCanUnequipEvent.EVENT.invoker().process(player, stack, original);
    }

    @Override
    public boolean compatibleWith(ItemStack stack, ItemStack other, boolean original) {
        return AccessoryCompatibleWithEvent.EVENT.invoker().process(stack, other, original);
    }

    @Override
    public boolean equip(Player player, ItemStack stack, EquipContext context) {
        return AccessoryEquipEvent.EVENT.invoker().process(player, stack, context);
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
        ImmutableMap.Builder<Item, Pair<AccessoryType, Boolean>> builder = new ImmutableMap.Builder<>();

        AccessoryOverrideTypesEvent.EVENT.invoker().process(builder);
        return builder.build();
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
    public boolean unequip(Player player, ItemStack stack) {
        return AccessoryUnequipEvent.EVENT.invoker().process(player, stack);
    }
}
