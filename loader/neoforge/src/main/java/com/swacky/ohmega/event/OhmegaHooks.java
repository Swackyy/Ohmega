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

    public static AccessoryTickEvent accessoryTickEventPre(Player player, ItemStack stack) {
        AccessoryTickEvent event = new AccessoryTickEvent.Pre(player, stack);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static void accessoryTickEventPost(Player player, ItemStack stack) {
        NeoForge.EVENT_BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    public static AccessoryEquipEvent accessoryEquipEvent(Player player, ItemStack stack, AccessoryEquipEvent.Context context) {
        AccessoryEquipEvent event = new AccessoryEquipEvent(player, stack, context);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryUnequipEvent accessoryUnequipEvent(Player player, ItemStack stack) {
        AccessoryUnequipEvent event = new AccessoryUnequipEvent(player, stack);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryCanEquipEvent accessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanEquipEvent event = new AccessoryCanEquipEvent(player, stack, flag);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryCanUnequipEvent accessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanUnequipEvent event = new AccessoryCanUnequipEvent(player, stack, flag);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryUseEvent accessoryUseEvent(Player player, ItemStack stack) {
        AccessoryUseEvent event = new AccessoryUseEvent(player, stack);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static void accessoryAttributeModifiersEvent(Item item, ModifierHolder.Builder builder) {
        NeoForge.EVENT_BUS.post(new AccessoryAttributeModifiersEvent(item, builder));
    }
}
