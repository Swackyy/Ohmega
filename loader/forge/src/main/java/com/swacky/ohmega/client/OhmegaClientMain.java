package com.swacky.ohmega.client;

import com.swacky.ohmega.client.model.HaloModel;
import com.swacky.ohmega.client.model.ModelLayerRegistry;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class OhmegaClientMain {
    public static void bootstrap(FMLJavaModLoadingContext context) {
        // Bootstrap
        OhmegaClient.bootstrap();

        // Config
        context.registerConfig(ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());

        // Rendering
        ModelLayerRegistry.register(HaloModel.LOCATION, HaloModel::createDefinition);
    }
}
