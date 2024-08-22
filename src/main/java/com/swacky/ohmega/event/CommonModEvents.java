package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.ModNetworking;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Ohmega.MODID)
public class CommonModEvents {
    private static ImmutableMap<Item, IAccessory> boundAccessories = ImmutableMap.of();

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper -> boundAccessories = OhmegaHooks.bindAccessoriesEvent());
    }

    public static boolean isItemAccessoryBound(Item item) {
        return item instanceof IAccessory || boundAccessories.containsKey(item);
    }

    public static IAccessory getBoundAccessory(Item item) {
        return item instanceof IAccessory accessory ? accessory : boundAccessories.get(item);
    }
}
