package com.swacky.ohmega.api.client;

import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

/**
 * Contains some client-specific common code used throughout the mod that isn't enough to deserve a single dedicated class, so it instead got lumped in here
 */
public final class OhmegaClient {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier DEFAULT_EXTENSION_ID = Ohmega.id("default");
    public static final Identifier PACK_DARK_ID = Ohmega.id("dark");
    public static final String LINK_CROWDIN = "https://crowdin.com/project/ohmega";

    private static boolean bootstrapped = false;
    private static int nonLinearServicesCount = 0;
    private static int servicesCount = 0;
    private static boolean locked = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + OhmegaClient.class + " multiple times");
        }
    }

    public static void lock() {
        locked = true;

        if (nonLinearServicesCount == 0) {
            LOGGER.info("Loaded {} services", servicesCount);
        } else {
            LOGGER.info("Loaded {} services ({} non-linear)", servicesCount, nonLinearServicesCount);
        }
    }

    public static <T> T loadService(Class<T> clazz) {
        String serviceName = clazz.getName();

        if (!locked) {
            T impl = ServiceLoader.load(clazz).findFirst().orElseThrow(() ->
                    new RuntimeException("Could not load service '" + serviceName + "' as no implementation was found. Ensure your workspace contains Ohmega on the runtime classpath"));
            String implName = impl.getClass().getName();

            if (bootstrapped) {
                LOGGER.debug("Loaded implementation '{}' for service '{}'", implName, serviceName);
            } else {
                nonLinearServicesCount++;
                LOGGER.debug("Non-linearly loaded implementation '{}' for service '{}'", implName, serviceName);
            }

            servicesCount++;

            return impl;
        } else {
            throw new IllegalStateException("Cannot load service '" + serviceName + "' after " + OhmegaClient.class + " has been locked");
        }
    }

    public static String widgetTranslationKey(String key) {
        return Ohmega.MODID + ".widget." + key;
    }
}
