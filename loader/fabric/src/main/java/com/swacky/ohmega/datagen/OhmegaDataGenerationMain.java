package com.swacky.ohmega.datagen;

import com.swacky.ohmega.datagen.client.OhmegaModelProvider;
import com.swacky.ohmega.datagen.client.OhmegaSplashProvider;
import com.swacky.ohmega.datagen.client.lang.locale.OhmegaEnPtProvider;
import com.swacky.ohmega.datagen.client.lang.locale.OhmegaEnUsProvider;
import com.swacky.ohmega.datagen.client.lang.locale.OhmegaEsEsProvider;
import com.swacky.ohmega.datagen.client.lang.locale.OhmegaItItProvider;
import com.swacky.ohmega.datagen.client.lang.locale.OhmegaNlNlProvider;
import com.swacky.ohmega.datagen.client.lang.locale.OhmegaRuRuProvider;
import com.swacky.ohmega.datagen.client.lang.locale.OhmegaUkUaProvider;
import com.swacky.ohmega.datagen.server.OhmegaAccessoryTypeProvider;
import com.swacky.ohmega.datagen.server.OhmegaTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public final class OhmegaDataGenerationMain implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(@NonNull FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // Client
        // Language locales
        pack.addProvider(OhmegaEnPtProvider::new);
        pack.addProvider(OhmegaEnUsProvider::new);
        pack.addProvider(OhmegaEsEsProvider::new);
        pack.addProvider(OhmegaItItProvider::new);
        pack.addProvider(OhmegaNlNlProvider::new);
        pack.addProvider(OhmegaRuRuProvider::new);
        pack.addProvider(OhmegaUkUaProvider::new);
        // Item models
        pack.addProvider(OhmegaModelProvider::new);
        // Splashes
        pack.addProvider(OhmegaSplashProvider::new);

        // Server
        pack.addProvider((FabricDataGenerator.Pack.Factory<OhmegaAccessoryTypeProvider>) OhmegaAccessoryTypeProvider::new);
        pack.addProvider(OhmegaTagProvider::new);
    }
}
