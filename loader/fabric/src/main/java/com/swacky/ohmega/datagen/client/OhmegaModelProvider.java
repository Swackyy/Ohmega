package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.init.OhmegaItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class OhmegaModelProvider extends FabricModelProvider {
    public OhmegaModelProvider(FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {}

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(OhmegaItems.ANGEL_RING, ModelTemplates.FLAT_ITEM);
    }
}
