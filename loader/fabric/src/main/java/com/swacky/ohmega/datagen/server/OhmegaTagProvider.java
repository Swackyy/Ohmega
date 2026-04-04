package com.swacky.ohmega.datagen.server;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class OhmegaTagProvider extends FabricTagsProvider<Item> {
    public OhmegaTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Registries.ITEM, lookup);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void addTags(HolderLookup.@NonNull Provider lookup) {
        builder(OhmegaTags.get(AccessoryType.UTILITY_ID)).add(OhmegaItems.getAngelRing().builtInRegistryHolder().key());
    }
}