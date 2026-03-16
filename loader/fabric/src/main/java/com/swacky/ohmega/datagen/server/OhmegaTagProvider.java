package com.swacky.ohmega.datagen.server;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

public class OhmegaTagProvider extends FabricTagProvider<Item> {
    public OhmegaTagProvider(FabricDataGenerator generator) {
        super(generator, Registry.ITEM);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void generateTags() {
        tag(OhmegaTags.get(AccessoryType.UTILITY_ID)).add(OhmegaItems.ANGEL_RING.builtInRegistryHolder().key());
    }
}
