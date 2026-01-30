package com.swacky.ohmega.common;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.resources.ResourceLocation;
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
    public static final ResourceLocation RELOAD_LISTENER_ID = OhmegaCommon.rl("accessory_type_manager");
    public static final ResourceLocation ACCESSORY_LOCATION = OhmegaCommon.rl("textures/gui/container/accessory_addon.png");
    public static final int ACCESSORY_ADDON_WIDTH = 26;
    public static final int ACCESSORY_ADDON_HEIGHT = 103;
    private static final Map<Item, IAccessory> BOUND_ACCESSORIES = new WeakHashMap<>();

    private static int NUM_SERVICES = 0;
    private static int NUM_SERVICES_COMMON = 0;
    private static Map<Item, AccessoryType> ACCESSORY_TYPE_OVERRIDES;

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

        NUM_SERVICES_COMMON = NUM_SERVICES;
        ACCESSORY_TYPE_OVERRIDES = OhmegaHooks.accessoryOverrideTypesEvent();
    }

    public static void bootstrapClient() {
        OhmegaBinds.bootstrap();
        OhmegaConfig.Client.bootstrap();

        LOGGER.info("Successfully loaded {} client services", NUM_SERVICES - NUM_SERVICES_COMMON);
    }

    public static @Nullable AccessoryType getTypeOverride(Item item) {
        return ACCESSORY_TYPE_OVERRIDES.get(item);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.tryBuild(MODID, path);
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
