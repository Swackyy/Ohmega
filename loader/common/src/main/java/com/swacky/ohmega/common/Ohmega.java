package com.swacky.ohmega.common;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.common.item.Accessory;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class Ohmega {
    public static final String MODID = "ohmega";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier RELOAD_LISTENER_ID = Ohmega.id("accessory_type_manager");
    private static final Map<Item, Accessory> BOUND_ACCESSORIES = new HashMap<>();

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
            AccessoryHelper.bootstrap();
            OhmegaDataComponents.bootstrap();
            OhmegaItems.bootstrap();
            OhmegaMenus.bootstrap();
            OhmegaConfig.Server.bootstrap();
            OhmegaHooks.bootstrap();
            OhmegaNetworking.bootstrap();
            LOGGER.info("Successfully loaded {} services", NUM_SERVICES);

            bootstrapped = true;
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + Ohmega.class.getName() + " multiple times");
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    /**
     * Use {@link AccessoryHelper#getAccessory}
     */
    public static Accessory getAccessory(Item item) {
        if (BOUND_ACCESSORIES.containsKey(item)) {
            return BOUND_ACCESSORIES.get(item);
        }

        if (item instanceof IAccessory binding) {
            Accessory accessory = new Accessory(binding);

            BOUND_ACCESSORIES.put(item, accessory);
            return accessory;
        }

        return null;
    }

    /**
     * Use {@link AccessoryHelper#isAccessory}
     */
    public static boolean isAccessory(Item item) {
        return getAccessory(item) != null;
    }

    /**
     * Use {@link AccessoryHelper#bindAccessory}
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        if (isAccessory(item) || item instanceof AirItem) {
            return false;
        }

        BOUND_ACCESSORIES.put(item, new Accessory(binding));
        return true;
    }
}
