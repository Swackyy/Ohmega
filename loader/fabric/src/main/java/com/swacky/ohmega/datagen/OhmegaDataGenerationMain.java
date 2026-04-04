package com.swacky.ohmega.datagen;

import com.swacky.ohmega.datagen.client.EnUsProvider;
import com.swacky.ohmega.datagen.client.EsEsProvider;
import com.swacky.ohmega.datagen.client.ItItProvider;
import com.swacky.ohmega.datagen.client.OhmegaModelProvider;
import com.swacky.ohmega.datagen.client.NlNlProvider;
import com.swacky.ohmega.datagen.client.RuRuProvider;
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
        // Locales
        pack.addProvider(EnUsProvider::new);
        pack.addProvider(EsEsProvider::new);
        pack.addProvider(ItItProvider::new);
        pack.addProvider(NlNlProvider::new);
        pack.addProvider(RuRuProvider::new);

        pack.addProvider(OhmegaModelProvider::new);

        // Server
        pack.addProvider((FabricDataGenerator.Pack.Factory<OhmegaAccessoryTypeProvider>) OhmegaAccessoryTypeProvider::new);
        pack.addProvider(OhmegaTagProvider::new);
    }
}
