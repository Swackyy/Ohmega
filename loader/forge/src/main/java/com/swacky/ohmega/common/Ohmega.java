package com.swacky.ohmega.common;

import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(OhmegaCommon.MODID)
public final class Ohmega {
    public static final Capability<AccessoryContainer> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});

    public Ohmega(FMLJavaModLoadingContext context) {
        OhmegaCommon.bootstrap();

        ModLoadingContext mlContext = ModLoadingContext.get();

        mlContext.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        IEventBus bus = context.getModEventBus();

        OhmegaItems.register(bus);
        OhmegaMenusImpl.register(bus);

        OhmegaNetworkingImpl.bootstrap();

        if (FMLEnvironment.dist.isClient()) {
            OhmegaCommon.bootstrapClient();

            mlContext.registerConfig(ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());
        }
    }
}