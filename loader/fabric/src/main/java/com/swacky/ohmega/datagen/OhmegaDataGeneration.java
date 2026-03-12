package com.swacky.ohmega.datagen;

import com.swacky.ohmega.datagen.client.OhmegaEnUsProvider;
import com.swacky.ohmega.datagen.client.OhmegaModelProvider;
import com.swacky.ohmega.datagen.server.OhmegaAccessoryTypeProvider;
import com.swacky.ohmega.datagen.server.OhmegaTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class OhmegaDataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // Client
        pack.addProvider(OhmegaEnUsProvider::new);
        pack.addProvider(OhmegaModelProvider::new);

        // Server
        pack.addProvider((FabricDataGenerator.Pack.Factory<OhmegaAccessoryTypeProvider>) OhmegaAccessoryTypeProvider::new);
        pack.addProvider(OhmegaTagProvider::new);
    }
}
