package com.swacky.ohmega.common.core.init;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ohmega.MODID);

    // This is simply a test and example accessory. Its code may be used as a reference to create your own.
    public static final RegistryObject<Item> ANGEL_RING = ITEMS.register("angel_ring", AngelRing::new);
}
