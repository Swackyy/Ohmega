package com.swacky.ohmega.common;

import com.swacky.ohmega.client.OhmegaClientMain;
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

@Mod(Ohmega.MODID)
public final class OhmegaMain {
    public OhmegaMain(IEventBus bus, Dist distro, ModContainer container) {
        // Bootstrap
        Ohmega.bootstrap();

        // Config
        container.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        // Registration
        OhmegaItemsImpl.register(bus);
        OhmegaMenusImpl.register(bus);
        OhmegaDataComponentsImpl.register(bus);
        OhmegaDataAttachments.register(bus);

        // Client entry
        if (distro.isClient()) {
            OhmegaClientMain.bootstrap(container);
        }
    }
}