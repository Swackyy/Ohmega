package com.swacky.ohmega.common;

import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(OhmegaCommon.MODID)
public final class Ohmega {
    public Ohmega(IEventBus bus, Dist distro) {
        OhmegaCommon.bootstrap();

        ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        OhmegaItems.register(bus);
        OhmegaMenusImpl.register(bus);
        OhmegaDataAttachments.register(bus);

        if (distro.isClient()) {
            OhmegaCommon.bootstrapClient();

            context.registerConfig(ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());
        }
    }
}