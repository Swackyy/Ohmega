package com.swacky.ohmega.common.core;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.core.init.OhmegaDataComponents;
import com.swacky.ohmega.common.core.init.OhmegaItems;
import com.swacky.ohmega.common.core.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.WeakHashMap;

// todo later: Complete scrolling overflow functionality for GUI
@Mod(Ohmega.MODID)
public class Ohmega {
    public static final String MODID = "ohmega";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Capability<AccessoryContainer> ACCESSORIES = CapabilityManager.get(new CapabilityToken<>(){});

    /**
     * These {@link Item} - {@link IAccessory} binding methods do not have documentation,
     * and it is just recommended that you use their mirrored counterparts in the {@link com.swacky.ohmega.api.AccessoryHelper} utility class
     */
    private static final Map<Item, IAccessory> BOUND_ACCESSORIES = new WeakHashMap<>();

    /**
     * Use {@link com.swacky.ohmega.api.AccessoryHelper#bindAccessory}
     */
    public static boolean bindAccessory(Item item, IAccessory binding) {
        if (item instanceof IAccessory || BOUND_ACCESSORIES.containsKey(item)) {
            return false;
        }
        BOUND_ACCESSORIES.put(item, binding);
        return true;
    }

    /**
     * Use {@link com.swacky.ohmega.api.AccessoryHelper#isItemAccessoryBound}
     */
    public static boolean isItemAccessoryBound(Item item) {
        return item instanceof IAccessory || BOUND_ACCESSORIES.containsKey(item);
    }

    /**
     * Use {@link com.swacky.ohmega.api.AccessoryHelper#getBoundAccessory}
     */
    public static IAccessory getBoundAccessory(Item item) {
        return item instanceof IAccessory accessory ? accessory : BOUND_ACCESSORIES.get(item);
    }

    // Future versions need constructor FMLJavaModLoadingContext parameter
    // "bus" var should just be context.get() as well
    @SuppressWarnings("removal")
    public Ohmega() {
        ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.CLIENT, OhmegaConfig.SPEC_CLIENT);
        context.registerConfig(ModConfig.Type.SERVER, OhmegaConfig.SPEC_SERVER);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        OhmegaItems.ITEMS.register(bus);
        OhmegaMenus.MENUS.register(bus);
        OhmegaDataComponents.DATA_COMPONENTS.register(bus);
    }

    public static ResourceLocation mcRl(String path) {
        return  ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}