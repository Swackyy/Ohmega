package com.swacky.ohmega.common;

import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Ohmega.MODID)
public final class OhmegaMain {
    public static final Capability<AccessoryContainer> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});

    public OhmegaMain(FMLJavaModLoadingContext context) {
        Ohmega.bootstrap();

        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        BusGroup group = context.getModBusGroup();

        OhmegaDataComponentsImpl.register(group);
        OhmegaItems.register(group);
        OhmegaMenusImpl.register(group);

        OhmegaNetworkingImpl.bootstrap();

        if (FMLEnvironment.dist.isClient()) {
            OhmegaClient.bootstrap();

            context.registerConfig(ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());
        }
    }
}