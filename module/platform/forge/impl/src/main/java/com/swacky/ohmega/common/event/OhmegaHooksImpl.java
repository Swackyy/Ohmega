package com.swacky.ohmega.common.event;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.event.AccessoryAllowWalkOnPowderSnowEvent;
import com.swacky.ohmega.api.common.event.AccessoryAttachDataEvent;
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
import com.swacky.ohmega.api.common.event.AccessoryToggleExtensionEvent;
import com.swacky.ohmega.api.common.event.AccessoryUnequipEvent;
import com.swacky.ohmega.api.common.event.AccessoryUseEvent;
import com.swacky.ohmega.api.common.event.OhmegaHooks;
import com.swacky.ohmega.api.common.event.RegisterAccessoryTypesEvent;
import com.swacky.ohmega.api.common.item.AccessoryContext;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanPair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoader;
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
        AccessoryTickEvent.Post.BUS.post(new AccessoryTickEvent.Post(entity, stack));
    }

    @Override
    public boolean accessoryTickPre(LivingEntity entity, ItemStack stack) {
        return AccessoryTickEvent.Pre.BUS.post(new AccessoryTickEvent.Pre(entity, stack));
    }

    @Override
    public boolean allowWalkOnPowderSnow(ItemStack stack, boolean original) {
        return AccessoryAllowWalkOnPowderSnowEvent.BUS.fire(new AccessoryAllowWalkOnPowderSnowEvent(stack, original)).returnValue;
    }

    @Override
    public void attachData(AccessoryData data, LivingEntity entity) {
        AccessoryAttachDataEvent.BUS.post(new AccessoryAttachDataEvent(data, entity));
    }

    @Override
    public boolean autoSync(ItemStack stack, boolean original) {
        return AccessoryAutoSyncEvent.BUS.fire(new AccessoryAutoSyncEvent(stack, original)).returnValue;
    }

    @Override
    public byte autoSyncModulo(ItemStack stack, byte original) {
        return AccessoryAutoSyncModuloEvent.BUS.fire(new AccessoryAutoSyncModuloEvent(stack, original)).returnValue;
    }

    @Override
    public boolean canEquip(LivingEntity entity, ItemStack stack, AccessoryContext context, boolean original) {
        return AccessoryCanEquipEvent.BUS.fire(new AccessoryCanEquipEvent(entity, stack, context, original)).returnValue;
    }

    @Override
    public boolean canUnequip(LivingEntity entity, ItemStack stack, boolean original) {
        return AccessoryCanUnequipEvent.BUS.fire(new AccessoryCanUnequipEvent(entity, stack, original)).returnValue;
    }

    @Override
    public boolean compatibleWith(ItemStack stack, ItemStack other, boolean original) {
        return AccessoryCompatibleWithEvent.BUS.fire(new AccessoryCompatibleWithEvent(stack, other, original)).returnValue;
    }

    @Override
    public boolean equip(LivingEntity entity, ItemStack stack, AccessoryContext context) {
        return AccessoryEquipEvent.BUS.post(new AccessoryEquipEvent(entity, stack, context));
    }

    @Override
    public SoundData equipSound(ItemStack stack, SoundData original) {
        return AccessoryEquipSoundEvent.BUS.fire(new AccessoryEquipSoundEvent(stack, original)).returnValue;
    }

    @Override
    public boolean isPiglinSafe(ItemStack stack, boolean original) {
        return AccessoryIsPiglinSafeEvent.BUS.fire(new AccessoryIsPiglinSafeEvent(stack, original)).returnValue;
    }

    @Override
    public BooleanBooleanPair keybindUse(Player player, ItemStack stack) {
        MutableBoolean shouldSynchronise = new MutableBoolean(false);

        return BooleanBooleanPair.of(AccessoryUseEvent.BUS.post(new AccessoryUseEvent(player, stack, shouldSynchronise)), shouldSynchronise.booleanValue());
    }

    @Override
    public double mobVisibility(ItemStack stack, Entity targetingEntity, double original) {
        return AccessoryMobVisibilityEvent.BUS.fire(new AccessoryMobVisibilityEvent(stack, targetingEntity, original)).returnValue;
    }

    @Override
    public Map<Item, BooleanObjectPair<AccessoryType>> overrideTypes() {
        Map<Item, BooleanObjectPair<AccessoryType>> map = new IdentityHashMap<>();

        ModLoader.postEvent(new AccessoryOverrideTypesEvent(map));
        return map;
    }

    @Override
    public boolean preferInventoryTick(ItemStack stack, boolean original) {
        return AccessoryPreferInventoryTickEvent.BUS.fire(new AccessoryPreferInventoryTickEvent(stack, original)).returnValue;
    }

    @Override
    public boolean preferVanillaUse(ItemStack stack, boolean original) {
        return AccessoryPreferVanillaUseEvent.BUS.fire(new AccessoryPreferVanillaUseEvent(stack, original)).returnValue;
    }

    @Override
    public Map<Identifier, AccessoryType> registerAccessoryTypes() {
        Map<Identifier, AccessoryType> map = new HashMap<>();

        RegisterAccessoryTypesEvent.BUS.post(new RegisterAccessoryTypesEvent(map));
        return map;
    }

    @Override
    public boolean shouldDropOnDeath(@NonNull ItemStack stack, @NonNull LivingEntity entity, boolean original) {
        return AccessoryShouldDropOnDeathEvent.BUS.fire(new AccessoryShouldDropOnDeathEvent(stack, entity, original)).returnValue;
    }

    @Override
    public void toggleExtensionEventPost(@NonNull IAccessoryMenu accessoryMenu, boolean value) {
        AccessoryToggleExtensionEvent.Post.BUS.post(new AccessoryToggleExtensionEvent.Post(accessoryMenu, value));
    }

    @Override
    public boolean toggleExtensionEventPre(@NonNull IAccessoryMenu accessoryMenu, boolean value) {
        return AccessoryToggleExtensionEvent.Pre.BUS.post(new AccessoryToggleExtensionEvent.Pre(accessoryMenu, value));
    }

    @Override
    public boolean unequip(LivingEntity entity, ItemStack stack, AccessoryContext context) {
        return AccessoryUnequipEvent.BUS.post(new AccessoryUnequipEvent(entity, stack, context));
    }
}
