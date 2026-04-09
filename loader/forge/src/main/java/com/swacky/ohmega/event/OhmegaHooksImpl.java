package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.api.SoundData;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryBind() {
        ModLoader.postEvent(new AccessoryBindEvent());
    }

    @Override
    public void accessoryTickPost(Player player, ItemStack stack) {
        AccessoryTickEvent.Post.BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    @Override
    public boolean accessoryTickPre(Player player, ItemStack stack) {
        return AccessoryTickEvent.Pre.BUS.post(new AccessoryTickEvent.Pre(player, stack));
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
    public boolean canEquip(Player player, ItemStack stack, EquipContext context, boolean original) {
        AccessoryCanEquipEvent event = new AccessoryCanEquipEvent(player, stack, context, original);

        AccessoryCanEquipEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean canUnequip(Player player, ItemStack stack, boolean original) {
        AccessoryCanUnequipEvent event = new AccessoryCanUnequipEvent(player, stack, original);

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
    public boolean equip(Player player, ItemStack stack, EquipContext context) {
        return AccessoryEquipEvent.BUS.post(new AccessoryEquipEvent(player, stack, context));
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
        AccessoryOverrideTypesEvent event = new AccessoryOverrideTypesEvent();

        ModLoader.postEvent(event);
        return event.getOverrides();
    }

    @Override
    public boolean preferVanillaUse(ItemStack stack, boolean original) {
        AccessoryPreferVanillaUseEvent event = new AccessoryPreferVanillaUseEvent(stack, original);

        AccessoryPreferVanillaUseEvent.BUS.post(event);
        return event.returnValue;
    }

    @Override
    public Map<Identifier, AccessoryType> registerAccessoryTypes() {
        RegisterAccessoryTypesEvent event = new RegisterAccessoryTypesEvent();

        RegisterAccessoryTypesEvent.BUS.post(event);
        return event.getTypes();
    }

    @Override
    public boolean unequip(Player player, ItemStack stack) {
        return AccessoryUnequipEvent.BUS.post(new AccessoryUnequipEvent(player, stack));
    }
}
