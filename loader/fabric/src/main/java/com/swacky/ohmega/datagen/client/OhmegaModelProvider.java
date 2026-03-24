package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.init.OhmegaItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import org.jspecify.annotations.NonNull;

public class OhmegaModelProvider extends FabricModelProvider {
    public OhmegaModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators generator) {}

    @Override
    public void generateItemModels(@NonNull ItemModelGenerators generator) {
        generator.generateFlatItem(OhmegaItems.ANGEL_RING, ModelTemplates.FLAT_ITEM);
    }
}
