package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.ModifierHolder;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoader;

public class OhmegaHooks {
    public static ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        AccessoryOverrideTypesEvent event = new AccessoryOverrideTypesEvent();
        ModLoader.postEvent(event);
        return event.get();
    }

    public static boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return AccessoryTickEvent.Pre.BUS.post(new AccessoryTickEvent.Pre(player, stack));
    }

    public static void accessoryTickEventPost(Player player, ItemStack stack) {
        AccessoryTickEvent.Post.BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    public static boolean accessoryEquipEvent(Player player, ItemStack stack, AccessoryEquipEvent.Context context) {
        return AccessoryEquipEvent.BUS.post(new AccessoryEquipEvent(player, stack, context));
    }

    public static boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return AccessoryUnequipEvent.BUS.post(new AccessoryUnequipEvent(player, stack));
    }

    public static boolean accessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanEquipEvent event = new AccessoryCanEquipEvent(player, stack, flag);
        AccessoryCanEquipEvent.BUS.post(event);
        return event.getReturnValue();
    }

    public static boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanUnequipEvent event = new AccessoryCanUnequipEvent(player, stack, flag);
        AccessoryCanUnequipEvent.BUS.post(event);
        return event.getReturnValue();
    }

    public static boolean accessoryUseEvent(Player player, ItemStack stack) {
        return AccessoryUseEvent.BUS.post(new AccessoryUseEvent(player, stack));
    }

    public static void accessoryAttributeModifiersEvent(Item item, ModifierHolder.Builder builder) {
        AccessoryAttributeModifiersEvent.BUS.post(new AccessoryAttributeModifiersEvent(item, builder));
    }
}
