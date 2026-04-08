package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.AccessoryAttributeModifiersEvent;
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
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        NeoForge.EVENT_BUS.post(new AccessoryAttributeModifiersEvent(stack, builder));
    }

    @Override
    public boolean accessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanEquipEvent(player, stack, context, initial)).returnValue;
    }

    @Override
    public boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial) {
        return NeoForge.EVENT_BUS.post(new AccessoryCanUnequipEvent(player, stack, initial)).returnValue;
    }

    @Override
    public boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return NeoForge.EVENT_BUS.post(new AccessoryEquipEvent(player, stack, context)).isCanceled();
    }

    @Override
    public Map<Item, Pair<AccessoryType, Boolean>> accessoryOverrideTypesEvent() {
        return ModLoader.postEventWithReturn(new AccessoryOverrideTypesEvent()).getOverrides();
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

    @Override
    public Map<Identifier, AccessoryType> registerAccessoryTypesEvent() {
        return NeoForge.EVENT_BUS.post(new RegisterAccessoryTypesEvent()).getTypes();
    }
}
