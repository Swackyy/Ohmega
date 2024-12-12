package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.item.AngelRing;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OhmegaItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OhmegaCommon.MODID);

    // This is simply a test and example accessory. Its code may be used as a reference to create your own.
    public static final RegistryObject<Item> ANGEL_RING = ITEMS.register("angel_ring", () -> new AngelRing(new Item.Properties().stacksTo(1)));
}
