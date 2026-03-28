package com.swacky.ohmega.client;

import com.swacky.ohmega.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

public final class OhmegaClient {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier ACCESSORY_LOCATION = Ohmega.id("textures/gui/container/accessory_addon.png");
    public static final int ACCESSORY_ADDON_WIDTH = 26;
    public static final int ACCESSORY_ADDON_HEIGHT = 103;

    private static int NUM_SERVICES = 0;

    public static <T> T loadService(Class<T> clazz) {
        String name = clazz.getName();
        T service = ServiceLoader.load(clazz).findFirst().orElseThrow(() ->
                new RuntimeException("Could not load service '" + name + "' as no implementation was found"));
        NUM_SERVICES++;

        LOGGER.debug("Loaded implementation '{}' for service '{}'", service.getClass().getName(), name);
        return service;
    }

    public static void bootstrap() {
        // Bootstrap
        AccessoryRenderStateData.bootstrap();
        OhmegaBinds.bootstrap();
        OhmegaConfig.Client.bootstrap();
        LOGGER.info("Successfully loaded {} client services", NUM_SERVICES);
    }
}
