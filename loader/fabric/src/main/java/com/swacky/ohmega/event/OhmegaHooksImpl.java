package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        return (ImmutableMap<Item, AccessoryType>) AccessoryOverrideTypesEvent.EVENT.invoker().process(ImmutableMap.of());
    }

    @Override
    public boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return AccessoryTickEvent.Pre.EVENT.invoker().process(player, stack);
    }

    @Override
    public void accessoryTickEventPost(Player player, ItemStack stack) {
        AccessoryTickEvent.Post.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return AccessoryEquipEvent.EVENT.invoker().process(player, stack, context);
    }

    @Override
    public boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return AccessoryUnequipEvent.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean accessoryCanEquipEvent(Player player, ItemStack stack, boolean flag) {
        return AccessoryCanEquipEvent.EVENT.invoker().process(player, stack, flag);
    }

    @Override
    public boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean flag) {
        return AccessoryCanUnequipEvent.EVENT.invoker().process(player, stack, flag);
    }

    @Override
    public boolean accessoryUseEvent(Player player, ItemStack stack) {
        return AccessoryUseEvent.EVENT.invoker().process(player, stack);
    }

    @Override
    public void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        AccessoryAttributeModifiersEvent.EVENT.invoker().process(stack, builder);
    }
}
