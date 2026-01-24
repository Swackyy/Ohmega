package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class OhmegaItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OhmegaCommon.MODID);

    public static final RegistryObject<Item> ANGEL_RING = register("angel_ring", () -> new AngelRing(new Item.Properties().stacksTo(1)));

    private static <T extends Item> RegistryObject<T> register(String id, Supplier<T> supplier) {
        return ITEMS.register(id, supplier);
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
