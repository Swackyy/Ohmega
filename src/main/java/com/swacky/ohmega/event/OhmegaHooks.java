package com.swacky.ohmega.event;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.events.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

// todo: make attr mods on compound tag with an active bool portion if they are marked only active when the accessory's active tag is true
// todo: make active only attr mods not active themselves by default, fixing the toggling issue
public class OhmegaHooks {
    public static AccessoryTickEvent accessoryTickEventPre(Player player, ItemStack stack) {
        AccessoryTickEvent event = new AccessoryTickEvent(Phase.PRE, player, stack);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void accessoryTickEventPost(Player player, ItemStack stack) {
        MinecraftForge.EVENT_BUS.post(new AccessoryTickEvent(Phase.POST, player, stack));
    }

    public static AccessoryEquipEvent accessoryEquipEvent(Player player, ItemStack stack) {
        AccessoryEquipEvent event = new AccessoryEquipEvent(player, stack);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryUnequipEvent accessoryUnequipEvent(Player player, ItemStack stack) {
        AccessoryUnequipEvent event = new AccessoryUnequipEvent(player, stack);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryCanEquipEvent accessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanEquipEvent event = new AccessoryCanEquipEvent(player, stack, flag);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryCanUnequipEvent accessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanUnequipEvent event = new AccessoryCanUnequipEvent(player, stack, flag);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static AccessoryUseEvent accessoryUseEvent(Player player, ItemStack stack) {
        AccessoryUseEvent event = new AccessoryUseEvent(player, stack);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void accessoryAttributeModifiersEvent(Item item, IAccessory.ModifierBuilder modifiers) {
        MinecraftForge.EVENT_BUS.post(new AccessoryAttributeModifiersEvent(item, modifiers));
    }
}
