package com.swacky.ohmega.datagen;

import com.swacky.ohmega.datagen.client.OhmegaItemModelProvider;
import com.swacky.ohmega.datagen.client.lang.OhmegaEnUsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class OhmegaDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(OhmegaItemModelProvider::new);
        pack.addProvider(OhmegaEnUsProvider::new);
    }
}
