package com.swacky.ohmega.datagen;

import com.swacky.ohmega.datagen.client.OhmegaEnUsProvider;
import com.swacky.ohmega.datagen.client.OhmegaEsEsProvider;
import com.swacky.ohmega.datagen.client.OhmegaItItProvider;
import com.swacky.ohmega.datagen.client.OhmegaModelProvider;
import com.swacky.ohmega.datagen.client.OhmegaNlNlProvider;
import com.swacky.ohmega.datagen.client.OhmegaRuRuProvider;
import com.swacky.ohmega.datagen.server.OhmegaAccessoryTypeProvider;
import com.swacky.ohmega.datagen.server.OhmegaTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jspecify.annotations.NonNull;

public class OhmegaDataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(@NonNull FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // Client
        // Locales
        pack.addProvider(OhmegaEnUsProvider::new);
        pack.addProvider(OhmegaEsEsProvider::new);
        pack.addProvider(OhmegaItItProvider::new);
        pack.addProvider(OhmegaNlNlProvider::new);
        pack.addProvider(OhmegaRuRuProvider::new);

        pack.addProvider(OhmegaModelProvider::new);

        // Server
        pack.addProvider((FabricDataGenerator.Pack.Factory<OhmegaAccessoryTypeProvider>) OhmegaAccessoryTypeProvider::new);
        pack.addProvider(OhmegaTagProvider::new);
    }
}
