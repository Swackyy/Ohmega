package com.swacky.ohmega.datagen.server;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class OhmegaTagProvider extends FabricTagProvider<Item> {
    public OhmegaTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Registries.ITEM, lookup);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(OhmegaTags.get(AccessoryType.UTILITY_ID)).add(OhmegaItems.ANGEL_RING.builtInRegistryHolder().key());
    }
}
