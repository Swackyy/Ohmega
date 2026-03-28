package com.swacky.ohmega.common;

import com.swacky.ohmega.client.OhmegaClientMain;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItemsImpl;
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
        // Bootstrap
        Ohmega.bootstrap();

        // Config
        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        // Registration
        BusGroup group = context.getModBusGroup();

        OhmegaDataComponentsImpl.register(group);
        OhmegaItemsImpl.register(group);
        OhmegaMenusImpl.register(group);

        OhmegaNetworkingImpl.bootstrap();

        // Client entry
        if (FMLEnvironment.dist.isClient()) {
            OhmegaClientMain.bootstrap(context);
        }
    }
}