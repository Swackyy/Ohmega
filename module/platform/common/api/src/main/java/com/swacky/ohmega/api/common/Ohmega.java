package com.swacky.ohmega.api.common;

import com.swacky.ohmega.api.IOhmegaEntrypoint;
import com.swacky.ohmega.api.OhmegaEntrypoint;
import com.swacky.ohmega.api.util.LogicalSide;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ServiceLoader;

/**
 * Contains some common code used throughout the mod that isn't enough to deserve a single dedicated class, so it instead got lumped in here
 */
// todo: kit out this and other classes moved to api with some clean nullability annotations
public final class Ohmega {
    public static final String MODID = "ohmega";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE = "This method was called without a defined functional method body. Implement it in your mixin class";

    private static boolean bootstrapped = false;
    private static int nonLinearServicesCount = 0;
    private static int servicesCount = 0;
    private static boolean locked = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + Ohmega.class + " multiple times");
        }
    }

    public static void lock() {
        locked = true;

        if (nonLinearServicesCount == 0) {
            LOGGER.info("Loaded {} common services", servicesCount);
        } else {
            LOGGER.info("Loaded {} common services ({} non-linear)", servicesCount, nonLinearServicesCount);
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
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
            throw new IllegalStateException("Cannot load service '" + serviceName + "' after " + Ohmega.class + " has been locked");
        }
    }

    public static void invokeEntrypoint(@Nullable LogicalSide metadataSide, @NonNull IOhmegaEntrypoint entrypoint) {
        Class<? extends IOhmegaEntrypoint> clazz = entrypoint.getClass();
        OhmegaEntrypoint annotation;

        if (clazz.isAnnotationPresent(OhmegaEntrypoint.class)) {
            annotation = clazz.getAnnotation(OhmegaEntrypoint.class);
        } else {
            entrypoint.invoke();
            return;
        }

        LogicalSide annotationSide = annotation.value();
        String name = clazz.getName();

        if (annotationSide == metadataSide) {
            try {
                entrypoint.invoke();
            } catch (Exception e) {
                throw new RuntimeException("Entrypoint '" + name + "' could not be invoked", e);
            }
        } else {
            throw new IllegalStateException("Entrypoint '" + name + "' could not be invoked, logical side metadata specification (" + metadataSide + ") differs from annotation (" + annotationSide + ')');
        }
    }
}
