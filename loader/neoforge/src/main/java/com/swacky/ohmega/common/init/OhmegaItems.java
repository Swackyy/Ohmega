package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.item.AngelRing;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class OhmegaItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OhmegaCommon.MODID);

    // This is simply a test and example accessory. Its code may be used as a reference to create your own.
    public static final Supplier<Item> ANGEL_RING = register("angel_ring", () -> new AngelRing(new Item.Properties().stacksTo(1)));

    private static <T extends Item> Supplier<T> register(String id, Supplier<T> sup) {
        return ITEMS.register(id, sup);
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
