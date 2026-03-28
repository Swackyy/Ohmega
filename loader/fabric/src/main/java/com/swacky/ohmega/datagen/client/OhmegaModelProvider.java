package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.init.OhmegaItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import org.jspecify.annotations.NonNull;

public class OhmegaModelProvider extends FabricModelProvider {
    public OhmegaModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators generator) {}

    @Override
    public void generateItemModels(@NonNull ItemModelGenerators generator) {
        generator.generateFlatItem(OhmegaItems.getAngelRing(), ModelTemplates.FLAT_ITEM);
    }
}
