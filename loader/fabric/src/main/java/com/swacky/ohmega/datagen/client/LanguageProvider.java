package com.swacky.ohmega.datagen.client;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public abstract class LanguageProvider implements DataProvider {
    private final FabricDataGenerator generator;
    private final String languageNamespace;
    private final Map<String, String> data = new TreeMap<>();

    public LanguageProvider(FabricDataGenerator generator, String languageNamespace) {
        this.generator = generator;
        this.languageNamespace = languageNamespace;
    }

    public abstract void generateTranslations();

    @Override
    public void run(CachedOutput output) {
        generateTranslations();

        if (!data.isEmpty()) {
            JsonObject json = new JsonObject();

            data.forEach(json::addProperty);
            try {
                DataProvider.saveStable(output, json,
                        this.generator.getOutputFolder(DataGenerator.Target.RESOURCE_PACK)
                                .resolve(generator.getModId()).resolve("lang")
                                .resolve(languageNamespace + ".json"));
            } catch (IOException e) {
                throw new RuntimeException("Could not write language '" + languageNamespace + "' data for generator with namespace '" + generator.getModId() + '\'', e);
            }
        }
    }

    @Override
    public @NonNull String getName() {
        return "Language '" + languageNamespace + "' for namespace '" + generator.getModId() + '\'';
    }

    protected void add(String key, String value) {
        data.put(key, value);
    }
}
