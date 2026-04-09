package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.SoundData;
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
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryBind() {
        ModLoader.postEvent(new AccessoryBindEvent());
    }

    @Override
    public void accessoryTickPost(Player player, ItemStack stack) {
        NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    @Override
    public boolean accessoryTickPre(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Pre(player, stack)).isCanceled();
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
    public boolean canEquip(Player player, ItemStack stack, EquipContext context, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanEquipEvent(player, stack, context, original)).returnValue;
    }

    @Override
    public boolean canUnequip(Player player, ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanUnequipEvent(player, stack, original)).returnValue;
    }

    @Override
    public boolean compatibleWith(ItemStack stack, ItemStack other, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryCompatibleWithEvent(stack, other, original)).returnValue;
    }

    @Override
    public boolean equip(Player player, ItemStack stack, EquipContext context) {
        return NeoForge.EVENT_BUS.post(new AccessoryEquipEvent(player, stack, context)).isCanceled();
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
        return ModLoader.postEventWithReturn(new AccessoryOverrideTypesEvent()).getOverrides();
    }

    @Override
    public boolean preferVanillaUse(ItemStack stack, boolean original) {
        return NeoForge.EVENT_BUS.post(new AccessoryPreferVanillaUseEvent(stack, original)).returnValue;
    }

    @Override
    public Map<Identifier, AccessoryType> registerAccessoryTypes() {
        return NeoForge.EVENT_BUS.post(new RegisterAccessoryTypesEvent()).getTypes();
    }

    @Override
    public boolean unequip(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryUnequipEvent(player, stack)).isCanceled();
    }
}
