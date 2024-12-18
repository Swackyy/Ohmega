package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class OhmegaItems {
    // This is simply a test and example accessory. Its code may be used as a reference to create your own.
    public static final Item ANGEL_RING = register("angel_ring", AngelRing::new, new Item.Properties().stacksTo(1));

    private static <T extends Item> T register(String id, Function<Item.Properties, T> function, Item.Properties props) {
        return Registry.register(BuiltInRegistries.ITEM, OhmegaCommon.rl(id), function.apply(props));
    }

    public static void init() {}
}
