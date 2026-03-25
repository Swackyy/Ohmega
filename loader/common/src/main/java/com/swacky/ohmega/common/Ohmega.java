package com.swacky.ohmega.common;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

public final class Ohmega {
    public static final String MODID = "ohmega";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier RELOAD_LISTENER_ID = Ohmega.id("accessory_type_manager");
    private static final Map<Item, IAccessory> BOUND_ACCESSORIES = new WeakHashMap<>();

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
        AccessoryHelper.bootstrap();
        OhmegaDataComponents.bootstrap();
        OhmegaMenus.bootstrap();
        OhmegaConfig.Server.bootstrap();
        OhmegaHooks.bootstrap();
        OhmegaNetworking.bootstrap();
        LOGGER.info("Successfully loaded {} services", NUM_SERVICES);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    /**
     * Use {@link AccessoryHelper#getBoundAccessory}
     */
    public static IAccessory getBoundAccessory(Item item) {
        return item instanceof IAccessory accessory ? accessory : BOUND_ACCESSORIES.get(item);
    }

    /**
     * Use {@link AccessoryHelper#isItemAccessoryBound}
     */
    public static boolean isItemAccessoryBound(Item item) {
        return getBoundAccessory(item) != null;
    }

    /**
     * Use {@link AccessoryHelper#bindAccessory}
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        if (isItemAccessoryBound(item) || item instanceof AirItem) {
            return false;
        }

        BOUND_ACCESSORIES.put(item, binding);
        return true;
    }
}
