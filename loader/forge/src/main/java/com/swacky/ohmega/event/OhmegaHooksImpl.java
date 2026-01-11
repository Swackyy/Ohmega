package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.*;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;

import java.util.Map;
import java.util.Set;

public final class OhmegaHooksImpl implements OhmegaHooks.Service {
    @Override
    public void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        MinecraftForge.EVENT_BUS.post(new AccessoryAttributeModifiersEvent(stack, builder));
    }

    @Override
    public boolean accessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial) {
        AccessoryCanEquipEvent event = new AccessoryCanEquipEvent(player, stack, context, initial);

        MinecraftForge.EVENT_BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial) {
        AccessoryCanUnequipEvent event = new AccessoryCanUnequipEvent(player, stack, initial);

        MinecraftForge.EVENT_BUS.post(event);
        return event.returnValue;
    }

    @Override
    public boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return MinecraftForge.EVENT_BUS.post(new AccessoryEquipEvent(player, stack, context));
    }

    @Override
    public Map<Item, AccessoryType> accessoryOverrideTypesEvent() {
        AccessoryOverrideTypesEvent event = new AccessoryOverrideTypesEvent();

        ModLoader.get().postEvent(event);
        return event.overrides;
    }

    @Override
    public void accessoryTickEventPost(Player player, ItemStack stack) {
        MinecraftForge.EVENT_BUS.post(new AccessoryTickEvent.Post(player, stack));
    }

    @Override
    public boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return MinecraftForge.EVENT_BUS.post(new AccessoryTickEvent.Pre(player, stack));
    }

    @Override
    public boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return MinecraftForge.EVENT_BUS.post(new AccessoryUnequipEvent(player, stack));
    }

    @Override
    public boolean accessoryUseEvent(Player player, ItemStack stack) {
        return MinecraftForge.EVENT_BUS.post(new AccessoryUseEvent(player, stack));
    }

    @Override
    public Set<AccessoryType> registerAccessoryTypesEvent() {
        RegisterAccessoryTypesEvent event = new RegisterAccessoryTypesEvent();

        MinecraftForge.EVENT_BUS.post(event);
        return event.types;
    }
}
