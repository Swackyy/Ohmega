package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public final class OhmegaModelProvider extends ModelProvider {
    public OhmegaModelProvider(PackOutput output) {
        super(output, OhmegaCommon.MODID);
    }

    @Override
    protected @NonNull Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.of(OhmegaItems.ANGEL_RING);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blocks, ItemModelGenerators items) {
        items.generateFlatItem(OhmegaItems.ANGEL_RING.get(), ModelTemplates.FLAT_ITEM);
    }
}
