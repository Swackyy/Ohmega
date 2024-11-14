package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.ModNetworking;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Ohmega.MODID)
public class CommonModEvents {
    private static ImmutableMap<Item, AccessoryType> accessoryTypeOverrides = ImmutableMap.of();

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetworking.register();
            CommonModEvents.accessoryTypeOverrides = OhmegaHooks.accessoryOverrideTypesEvent();
        });
    }

    public static ImmutableMap<Item, AccessoryType> getAccessoryTypeOverrides() {
        return CommonModEvents.accessoryTypeOverrides;
    }
}
