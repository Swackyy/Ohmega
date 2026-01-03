package com.swacky.ohmega.common;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

public final class OhmegaCommon {
    public static final String MODID = "ohmega";
    public static final Logger LOGGER = LogManager.getLogger("Ohmega");
    public static final Identifier ACCESSORY_LOCATION = OhmegaCommon.id("textures/gui/container/accessory_addon.png");
    public static final int ACCESSORY_ADDON_WIDTH = 26;
    public static final int ACCESSORY_ADDON_HEIGHT = 103;
    private static final Map<Item, IAccessory> BOUND_ACCESSORIES = new WeakHashMap<>();

    private static ImmutableMap<Item, AccessoryType> ACCESSORY_TYPE_OVERRIDES;

    public static void bootstrap() {
        OhmegaConfig.bootstrap();

        ACCESSORY_TYPE_OVERRIDES = OhmegaHooks.accessoryOverrideTypesEvent();
    }

    public static <T> T loadService(Class<T> clazz) {
        String name = clazz.getName();
        T service = ServiceLoader.load(clazz).findFirst().orElseThrow(() ->
                new RuntimeException("Could not load service '" + name + "' as no implementation was found"));

        LOGGER.info("Loaded implementation '{}' for service '{}'", service.getClass().getName(), name);
        return service;
    }

    public static @Nullable AccessoryType getTypeOverride(Item item) {
        return ACCESSORY_TYPE_OVERRIDES.get(item);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    /**
     * Use {@link com.swacky.ohmega.api.AccessoryHelper#getBoundAccessory}
     */
    public static IAccessory getBoundAccessory(Item item) {
        return item instanceof IAccessory accessory ? accessory : BOUND_ACCESSORIES.get(item);
    }

    /**
     * Use {@link com.swacky.ohmega.api.AccessoryHelper#isItemAccessoryBound}
     */
    public static boolean isItemAccessoryBound(Item item) {
        return getBoundAccessory(item) != null;
    }

    /**
     * Use {@link com.swacky.ohmega.api.AccessoryHelper#bindAccessory}
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        if (isItemAccessoryBound(item) || item instanceof AirItem) {
            return false;
        }

        BOUND_ACCESSORIES.put(item, binding);
        return true;
    }
}
