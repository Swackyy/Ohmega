package com.swacky.ohmega.client;

import com.swacky.ohmega.client.screen.button.CrowdinButton;
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
        container.registerExtensionPoint(IConfigScreenFactory.class, (container0, parentScreen) -> {
            ConfigurationScreen configScreen = new ConfigurationScreen(container0, parentScreen);

            configScreen.addRenderableWidget(new CrowdinButton(configScreen));
            return configScreen;
        });
    }
}
