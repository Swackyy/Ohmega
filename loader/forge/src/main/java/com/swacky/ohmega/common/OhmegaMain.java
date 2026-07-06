package com.swacky.ohmega.common;

import com.swacky.ohmega.api.common.init.OhmegaArgumentTypes;
import com.swacky.ohmega.api.common.init.OhmegaCriteriaTriggersImpl;
import com.swacky.ohmega.api.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.api.common.init.OhmegaItemsImpl;
import com.swacky.ohmega.client.OhmegaClientMain;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Ohmega.MODID)
public final class OhmegaMain {
    public OhmegaMain(FMLJavaModLoadingContext context) {
        // Bootstrap
        Ohmega.bootstrap();
        OhmegaNetworkingImpl.bootstrap();

        // Config
        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        // Registration
        BusGroup group = context.getModBusGroup();

        OhmegaArgumentTypes.register(group);
        OhmegaCriteriaTriggersImpl.register(group);
        OhmegaDataComponentsImpl.register(group);
        OhmegaItemsImpl.register(group);

        // Client entry
        if (FMLEnvironment.dist.isClient()) {
            OhmegaClientMain.bootstrap(context);
        }
    }
}