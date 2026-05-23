package com.swacky.ohmega.client;

import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.client.screen.DefaultScreenExtension;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.config.OhmegaConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

public final class OhmegaClient {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String LINK_CROWDIN = "https://crowdin.com/project/ohmega";

    private static boolean bootstrapped = false;
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
        if (!bootstrapped) {
            // Bootstrap services
            AccessoryRenderStateData.bootstrap();
            OhmegaBinds.bootstrap();
            OhmegaConfig.Client.bootstrap();
            LOGGER.info("Successfully loaded {} client services", NUM_SERVICES);

            // Register screen extension
            AccessoryScreens.registerExtension(Ohmega.INTERFACE_ID, DefaultScreenExtension::new);

            bootstrapped = true;
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + OhmegaClient.class + " multiple times");
        }
    }
}
