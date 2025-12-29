package com.swacky.ohmega.common;

import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(OhmegaCommon.MODID)
public final class Ohmega {
    public Ohmega(IEventBus bus, Dist distro, ModContainer container) {
        OhmegaCommon.bootstrap();

        container.registerConfig(ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());
        container.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        OhmegaItems.register(bus);
        OhmegaMenusImpl.register(bus);
        OhmegaDataComponentsImpl.register(bus);
        OhmegaDataAttachments.register(bus);

        if (distro.isClient()) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}