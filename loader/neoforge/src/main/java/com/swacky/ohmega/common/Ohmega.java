package com.swacky.ohmega.common;

import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(OhmegaCommon.MODID)
public class Ohmega {
    public Ohmega(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, OhmegaConfig.SPEC_CLIENT);
        container.registerConfig(ModConfig.Type.SERVER, OhmegaConfig.SPEC_SERVER);

        OhmegaItems.register(bus);
        OhmegaMenus.register(bus);
        OhmegaDataComponents.register(bus);
        OhmegaDataAttachments.register(bus);
    }
}