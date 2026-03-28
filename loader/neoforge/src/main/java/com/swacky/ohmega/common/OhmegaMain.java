package com.swacky.ohmega.common;

import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItemsImpl;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Ohmega.MODID)
public final class OhmegaMain {
    public OhmegaMain(IEventBus bus, Dist distro, ModContainer container) {
        Ohmega.bootstrap();

        container.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        OhmegaItemsImpl.register(bus);
        OhmegaMenusImpl.register(bus);
        OhmegaDataComponentsImpl.register(bus);
        OhmegaDataAttachments.register(bus);

        if (distro.isClient()) {
            OhmegaClient.bootstrap();

            container.registerConfig(ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}