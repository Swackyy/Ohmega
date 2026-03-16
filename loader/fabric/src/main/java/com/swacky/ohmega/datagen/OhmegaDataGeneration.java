package com.swacky.ohmega.datagen;

import com.swacky.ohmega.datagen.client.OhmegaEnUsProvider;
import com.swacky.ohmega.datagen.client.OhmegaModelProvider;
import com.swacky.ohmega.datagen.server.OhmegaAccessoryTypeProvider;
import com.swacky.ohmega.datagen.server.OhmegaTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

@SuppressWarnings("unused")
public class OhmegaDataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        // Client
        generator.addProvider(OhmegaEnUsProvider::new);
        generator.addProvider(OhmegaModelProvider::new);

        // Server
        generator.addProvider(OhmegaAccessoryTypeProvider::new);
        generator.addProvider(OhmegaTagProvider::new);
    }
}
