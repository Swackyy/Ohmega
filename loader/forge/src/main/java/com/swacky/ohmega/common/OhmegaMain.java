package com.swacky.ohmega.common;

import com.swacky.ohmega.client.OhmegaClientMain;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaArgumentTypes;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItemsImpl;
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
    public static final Capability<AccessoryData> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>() {});

    public OhmegaMain(FMLJavaModLoadingContext context) {
        // Bootstrap
        Ohmega.bootstrap();
        OhmegaNetworkingImpl.bootstrap();

        // Config
        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        // Registration
        BusGroup group = context.getModBusGroup();

        OhmegaArgumentTypes.register(group);
        OhmegaDataComponentsImpl.register(group);
        OhmegaItemsImpl.register(group);

        // Client entry
        if (FMLEnvironment.dist.isClient()) {
            OhmegaClientMain.bootstrap(context);
        }
    }
}