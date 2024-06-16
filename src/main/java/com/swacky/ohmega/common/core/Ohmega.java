package com.swacky.ohmega.common.core;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.init.ModItems;
import com.swacky.ohmega.common.core.init.ModMenus;
import com.swacky.ohmega.event.ClientEvents;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ohmega.MODID)
public class Ohmega {
    public static final String MODID = "ohmega";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Capability<AccessoryContainer> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IAccessory> ACCESSORY_ITEM = CapabilityManager.get(new CapabilityToken<>(){});

    public Ohmega() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientEvents::clientSetup);

        ModMenus.MENUS.register(bus);
        ModItems.ITEMS.register(bus);
    }
}