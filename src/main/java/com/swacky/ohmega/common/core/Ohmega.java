package com.swacky.ohmega.common.core;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.init.ModMenus;
import com.swacky.ohmega.common.item.FlyRing;
import com.swacky.ohmega.event.ClientEvents;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ohmega.MODID)
public class Ohmega {
    public static final String MODID = "ohmega";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Capability<AccessoryContainer> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IAccessory> ACCESSORY_ITEM = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<Byte> ACCESSORY_SLOT = CapabilityManager.get(new CapabilityToken<>(){});

    public Ohmega() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientEvents::clientSetup);

        ModMenus.MENUS.register(bus);

        // This is simply a test and example accessory. Its code may be used as a reference to create your own.
        DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
        ITEMS.register("flyring", FlyRing::new);
        ITEMS.register(bus);

        ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "gui/accessory_slot_normal")));
        ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "gui/accessory_slot_utility")));
        ModelBakery.UNREFERENCED_TEXTURES.add(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "gui/accessory_slot_special")));
    }
}