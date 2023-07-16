package com.swacky.ohmega.common.core;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.init.ModMenus;
import com.swacky.ohmega.event.ClientEvents;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Ohmega.MODID)
public class Ohmega {
    public static final String MODID = "ohmega";
    public Ohmega() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientEvents::clientSetup);

        ModMenus.MENUS.register(bus);

        ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "gui/accessory_slot_normal")));
        ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "gui/accessory_slot_utility")));
        ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "gui/accessory_slot_special")));
    }

    public static final Capability<AccessoryContainer> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IAccessory> ACCESSORY_ITEM = CapabilityManager.get(new CapabilityToken<>(){});
}
