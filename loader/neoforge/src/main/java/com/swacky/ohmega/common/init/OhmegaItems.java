package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.item.AngelRing;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OhmegaItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OhmegaCommon.MODID);

    // This is simply a test and example accessory. Its code may be used as a reference to create your own.
    public static final DeferredItem<AngelRing> ANGEL_RING = ITEMS.registerItem("angel_ring", AngelRing::new, new Item.Properties().stacksTo(1));
}
