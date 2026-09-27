package com.swacky.ohmega.client;

import com.swacky.ohmega.api.client.model.ModelLayerRegistry;
import com.swacky.ohmega.client.config.OhmegaClientConfigImpl;
import com.swacky.ohmega.client.model.HaloModel;
import com.swacky.ohmega.client.screen.ConfigurationScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class OhmegaClientMain {
    public static void bootstrap(FMLJavaModLoadingContext context) {
        // Bootstrap
        OhmegaClientBootstrap.bootstrap();

        // Config
        context.registerConfig(ModConfig.Type.CLIENT, OhmegaClientConfigImpl.getSpec());
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((_, parentScreen) ->
                        new ConfigurationScreen(context.getContainer(), parentScreen)));

        // Rendering
        ModelLayerRegistry.register(HaloModel.LOCATION, HaloModel::createDefinition);
    }
}
