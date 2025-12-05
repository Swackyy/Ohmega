package com.swacky.ohmega.common;

import com.swacky.ohmega.common.dataattachment.AccessoryInvDataAttachment;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OhmegaCommon.MODID)
public class Ohmega {
    public static final Capability<AccessoryInvDataAttachment> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});

    public Ohmega(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, OhmegaConfig.SPEC_CLIENT);
        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfig.SPEC_SERVER);

        BusGroup group = context.getModBusGroup();

        OhmegaItems.register(group);
        OhmegaMenus.register(group);
        OhmegaDataComponents.register(group);
    }
}