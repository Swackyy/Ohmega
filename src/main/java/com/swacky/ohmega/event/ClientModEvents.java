package com.swacky.ohmega.event;

import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModBinds;
import com.swacky.ohmega.common.core.init.ModMenus;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Ohmega.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerKbs(RegisterKeyMappingsEvent event) {
        event.register(ModBinds.UTILITY_0);
        event.register(ModBinds.UTILITY_1);
        event.register(ModBinds.SPECIAL);
    }

    @SuppressWarnings("RedundantCast")
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ACCESSORY_INVENTORY.get(), (MenuScreens.ScreenConstructor<AccessoryInventoryMenu, AccessoryInventoryScreen>) AccessoryInventoryScreen::new);
            ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(Ohmega.MODID, "item/accessory_slot_normal")));
            ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(Ohmega.MODID, "item/accessory_slot_utility")));
            ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(Ohmega.MODID, "item/accessory_slot_special")));
        });
    }
}
