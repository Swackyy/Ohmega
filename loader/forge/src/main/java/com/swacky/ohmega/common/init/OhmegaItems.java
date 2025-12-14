package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.item.AngelRing;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class OhmegaItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OhmegaCommon.MODID);

    // This is simply a test and example accessory. Its code may be used as a reference to create your own.
    public static final RegistryObject<Item> ANGEL_RING = register("angel_ring", AngelRing::new, new Item.Properties().stacksTo(1));

    private static <T extends Item> RegistryObject<T> register(String id, Function<Item.Properties, T> function, Item.Properties props) {
        return ITEMS.register(id, () -> function.apply(props.setId(ResourceKey.create(Registries.ITEM, OhmegaCommon.id(id)))));
    }

    public static void register(BusGroup group) {
        ITEMS.register(group);
    }
}
