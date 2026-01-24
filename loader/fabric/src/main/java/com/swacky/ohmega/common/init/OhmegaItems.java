package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public final class OhmegaItems {
    public static final Item ANGEL_RING = register("angel_ring", new AngelRing(new Item.Properties().stacksTo(1)));

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(BuiltInRegistries.ITEM, OhmegaCommon.rl(id), item);
    }

    public static void init() {}
}
