package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        AccessoryAttributeModifiersEvent.EVENT.invoker().process(stack, builder);
    }

    @Override
    public boolean accessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial) {
        return AccessoryCanEquipEvent.EVENT.invoker().process(player, stack, context, initial);
    }

    @Override
    public boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial) {
        return AccessoryCanUnequipEvent.EVENT.invoker().process(player, stack, initial);
    }

    @Override
    public boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return AccessoryEquipEvent.EVENT.invoker().process(player, stack, context);
    }

    @Override
    public ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        return (ImmutableMap<Item, AccessoryType>) AccessoryOverrideTypesEvent.EVENT.invoker().process(ImmutableMap.of());
    }

    @Override
    public void accessoryTickEventPost(Player player, ItemStack stack) {
        AccessoryTickEvent.Post.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return AccessoryTickEvent.Pre.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return AccessoryUnequipEvent.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean accessoryUseEvent(Player player, ItemStack stack) {
        return AccessoryUseEvent.EVENT.invoker().process(player, stack);
    }

    @Override
    public Set<AccessoryType> registerAccessoryTypesEvent() {
        return RegisterAccessoryTypesEvent.EVENT.invoker().process();
    }
}
