package com.swacky.ohmega.common;

import com.swacky.ohmega.api.IAccessory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.WeakHashMap;

public class OhmegaCommon {
    public static final String MODID = "ohmega";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation ACCESSORY_LOCATION = OhmegaCommon.rl("textures/gui/container/accessory_addon.png");

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    /**
     * These {@link Item} - {@link IAccessory} binding methods do not have documentation,
     * and it is just recommended that you use their mirrored counterparts in the {@code AccessoryHelper} utility class
     */
    private static final Map<Item, IAccessory> BOUND_ACCESSORIES = new WeakHashMap<>();

    /**
     * Use {@code AccessoryHelper.bindAccessory}
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        if (item instanceof IAccessory || BOUND_ACCESSORIES.containsKey(item)) {
            return false;
        }
        BOUND_ACCESSORIES.put(item, binding);
        return true;
    }

    /**
     * Use {@code AccessoryHelper.isItemAccessoryBound}
     */
    public static boolean isItemAccessoryBound(Item item) {
        return item instanceof IAccessory || BOUND_ACCESSORIES.containsKey(item);
    }

    /**
     * Use {@code AccessoryHelper.getBoundAccessory}
     */
    public static IAccessory getBoundAccessory(Item item) {
        return item instanceof IAccessory accessory ? accessory : BOUND_ACCESSORIES.get(item);
    }
}
