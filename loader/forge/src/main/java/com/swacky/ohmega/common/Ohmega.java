package com.swacky.ohmega.common;

import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OhmegaCommon.MODID)
public class Ohmega {
    public static final Capability<AccessoryContainer> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});

    public Ohmega(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, OhmegaConfig.SPEC_CLIENT);
        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfig.SPEC_SERVER);

        IEventBus bus = context.getModEventBus();

        OhmegaItems.ITEMS.register(bus);
        OhmegaMenus.MENUS.register(bus);
        OhmegaDataComponents.DATA_COMPONENTS.register(bus);
    }
}