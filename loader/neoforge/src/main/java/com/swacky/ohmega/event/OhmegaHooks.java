package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.ModifierHolder;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;

public class OhmegaHooks {
    public static ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        AccessoryOverrideTypesEvent event = new AccessoryOverrideTypesEvent();
        ModLoader.postEvent(event);
        return event.get();
    }

    public static boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Pre(player, stack)).isCanceled();
    }

    public static void accessoryTickEventPost(Player player, ItemStack stack) {
        NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    public static boolean accessoryEquipEvent(Player player, ItemStack stack, AccessoryEquipEvent.Context context) {
        return NeoForge.EVENT_BUS.post(new AccessoryEquipEvent(player, stack, context)).isCanceled();
    }

    public static boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryUnequipEvent(player, stack)).isCanceled();
    }

    public static boolean accessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanEquipEvent(player, stack, flag)).getReturnValue();
    }

    public static boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanUnequipEvent(player, stack, flag)).getReturnValue();
    }

    public static boolean accessoryUseEvent(Player player, ItemStack stack) {
        return NeoForge.EVENT_BUS.post(new AccessoryUseEvent(player, stack)).isCanceled();
    }

    public static void accessoryAttributeModifiersEvent(Item item, ModifierHolder.Builder builder) {
        NeoForge.EVENT_BUS.post(new AccessoryAttributeModifiersEvent(item, builder));
    }
}
