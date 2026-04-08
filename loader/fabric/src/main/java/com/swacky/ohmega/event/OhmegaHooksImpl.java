package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.AccessoryAttributeModifiersEvent;
import com.swacky.ohmega.api.event.AccessoryBindEvent;
import com.swacky.ohmega.api.event.AccessoryCanEquipEvent;
import com.swacky.ohmega.api.event.AccessoryCanUnequipEvent;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.api.event.AccessoryOverrideTypesEvent;
import com.swacky.ohmega.api.event.AccessoryTickEvent;
import com.swacky.ohmega.api.event.AccessoryUnequipEvent;
import com.swacky.ohmega.api.event.AccessoryUseEvent;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.api.event.RegisterAccessoryTypesEvent;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        AccessoryAttributeModifiersEvent.EVENT.invoker().process(stack, builder);
    }

    @Override
    public void accessoryBindEvent() {
        AccessoryBindEvent.EVENT.invoker().process();
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
    public Map<Item, Pair<AccessoryType, Boolean>> accessoryOverrideTypesEvent() {
        ImmutableMap.Builder<Item, Pair<AccessoryType, Boolean>> builder = new ImmutableMap.Builder<>();

        AccessoryOverrideTypesEvent.EVENT.invoker().process(builder);
        return builder.build();
    }

    @Override
    public void accessoryTickPostEvent(Player player, ItemStack stack) {
        AccessoryTickEvent.Post.EVENT.invoker().process(player, stack);
    }

    @Override
    public boolean accessoryTickPreEvent(Player player, ItemStack stack) {
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
    public Map<Identifier, AccessoryType> registerAccessoryTypesEvent() {
        ImmutableMap.Builder<Identifier, AccessoryType> builder = new ImmutableMap.Builder<>();

        RegisterAccessoryTypesEvent.EVENT.invoker().process(builder);
        return builder.build();
    }
}
