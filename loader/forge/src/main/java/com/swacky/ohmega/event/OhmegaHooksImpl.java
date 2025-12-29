package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoader;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        AccessoryOverrideTypesEvent event = new AccessoryOverrideTypesEvent();
        ModLoader.postEvent(event);
        return event.get();
    }

    @Override
    public boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return AccessoryTickEvent.Pre.BUS.post(new AccessoryTickEvent.Pre(player, stack));
    }

    @Override
    public void accessoryTickEventPost(Player player, ItemStack stack) {
        AccessoryTickEvent.Post.BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    @Override
    public boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return AccessoryEquipEvent.BUS.post(new AccessoryEquipEvent(player, stack, context));
    }

    @Override
    public boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return AccessoryUnequipEvent.BUS.post(new AccessoryUnequipEvent(player, stack));
    }

    @Override
    public boolean accessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanEquipEvent event = new AccessoryCanEquipEvent(player, stack, flag);
        AccessoryCanEquipEvent.BUS.post(event);
        return event.getReturnValue();
    }

    @Override
    public boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        AccessoryCanUnequipEvent event = new AccessoryCanUnequipEvent(player, stack, flag);
        AccessoryCanUnequipEvent.BUS.post(event);
        return event.getReturnValue();
    }

    @Override
    public boolean accessoryUseEvent(Player player, ItemStack stack) {
        return AccessoryUseEvent.BUS.post(new AccessoryUseEvent(player, stack));
    }

    @Override
    public void accessoryAttributeModifiersEvent(Item item, AccessoryModifiers.Builder builder) {
        AccessoryAttributeModifiersEvent.BUS.post(new AccessoryAttributeModifiersEvent(item, builder));
    }
}
