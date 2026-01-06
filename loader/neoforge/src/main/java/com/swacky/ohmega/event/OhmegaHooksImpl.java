package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        NeoForge.EVENT_BUS.post(new AccessoryAttributeModifiersEvent(stack, builder));
    }

    @Override
    public boolean accessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanEquipEvent(player, stack, context, initial)).getReturnValue();
    }

    @Override
    public boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanUnequipEvent(player, stack, initial)).getReturnValue();
    }

    @Override
    public boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return NeoForge.EVENT_BUS.post(new AccessoryEquipEvent(player, stack, context)).isCanceled();
    }

    @Override
    public ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        AccessoryOverrideTypesEvent event = new AccessoryOverrideTypesEvent();
        ModLoader.postEvent(event);
        return event.get();
    }

    @Override
    public void accessoryTickEventPost(Player player, ItemStack stack) {
        NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    @Override
    public boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Pre(player, stack)).isCanceled();
    }

    @Override
    public boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryUnequipEvent(player, stack)).isCanceled();
    }

    @Override
    public boolean accessoryUseEvent(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryUseEvent(player, stack)).isCanceled();
    }
}
