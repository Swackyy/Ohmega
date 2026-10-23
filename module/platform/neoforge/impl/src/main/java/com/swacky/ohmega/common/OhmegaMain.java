package com.swacky.ohmega.common;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaArgumentTypes;
import com.swacky.ohmega.api.util.LogicalSide;
import com.swacky.ohmega.client.OhmegaClientMain;
import com.swacky.ohmega.common.config.OhmegaServerConfigImpl;
import com.swacky.ohmega.common.init.OhmegaCriteriaTriggersImpl;
import com.swacky.ohmega.common.init.OhmegaDataAttachmentsImpl;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItemsImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import java.util.function.Function;

@Mod(Ohmega.MODID)
public final class OhmegaMain {
    private static final Function<String, Boolean> MOD_LOADED = ModList.get()::isLoaded;

    public OhmegaMain(IEventBus bus, Dist distro, ModContainer container) {
        // Bootstrap
        OhmegaBootstrap.bootstrap();

        // Config
        container.registerConfig(ModConfig.Type.SERVER, OhmegaServerConfigImpl.getSpec());

        // Registration
        OhmegaArgumentTypes.register(bus);
        OhmegaCriteriaTriggersImpl.register(bus);
        OhmegaDataAttachmentsImpl.register(bus);
        OhmegaDataComponentsImpl.register(bus);
        OhmegaItemsImpl.register(bus);

        // Custom entrypoint invocation
        Ohmega.invokeEntrypointsService(entrypoint -> Ohmega.invokeEntrypoint(LogicalSide.COMMON, entrypoint, MOD_LOADED));
        Ohmega.invokeEntrypointsService(entrypoint -> Ohmega.invokeEntrypoint(LogicalSide.SERVER, entrypoint, MOD_LOADED));

        // Client entry
        if (distro.isClient()) {
            OhmegaClientMain.bootstrap(container);
        }
    }
}