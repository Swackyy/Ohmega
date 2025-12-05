package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.ModifierHolder;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("removal")
public class OhmegaHooks {
    public static ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        return (ImmutableMap<Item, AccessoryType>) AccessoryOverrideTypesCallback.EVENT.invoker().process(ImmutableMap.of());
    }

    public static boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return AccessoryTickPreCallback.EVENT.invoker().process(player, stack).isCanceled();
    }

    public static void accessoryTickEventPost(Player player, ItemStack stack) {
        AccessoryTickPostCallback.EVENT.invoker().process(player, stack);
    }

    public static boolean accessoryEquipEvent(Player player, ItemStack stack, AccessoryEquipCallback.Context context) {
        return AccessoryEquipCallback.EVENT.invoker().process(player, stack, context).isCanceled();
    }

    public static boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return AccessoryUnequipCallback.EVENT.invoker().process(player, stack).isCanceled();
    }

    public static boolean accessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
        return AccessoryCanEquipCallback.EVENT.invoker().process(player, stack, flag);
    }

    public static boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        return AccessoryCanUnequipCallback.EVENT.invoker().process(player, stack, flag);
    }

    public static boolean accessoryUseEvent(Player player, ItemStack stack) {
        return AccessoryUseCallback.EVENT.invoker().process(player, stack).isCanceled();
    }

    public static void accessoryAttributeModifiersEvent(Item item, ModifierHolder.Builder builder) {
        AccessoryAttributeModifiersCallback.EVENT.invoker().process(item, builder);
    }
}
