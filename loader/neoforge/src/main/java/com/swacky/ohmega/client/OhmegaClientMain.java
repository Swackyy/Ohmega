package com.swacky.ohmega.client;

import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class OhmegaClientMain {
    public static void bootstrap(ModContainer container) {
        // Bootstrap
        OhmegaClient.bootstrap();

        // Config
        container.registerConfig(ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
