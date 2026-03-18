package com.swacky.ohmega.datagen.client;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import org.jspecify.annotations.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.swacky.ohmega.common.accessorytype.AccessoryType.Deserializer.*;

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
    public void run(HashCache cache) {
        generateTranslations();

        if (!data.isEmpty()) {
            JsonObject json = new JsonObject();

            data.forEach(json::addProperty);

            Path path = generator.getOutputFolder()
                    .resolve("assets")
                    .resolve(generator.getModId())
                    .resolve("lang")
                    .resolve(languageNamespace + ".json");
            String jsonData = GSON.toJson(data);
            String hash = DataProvider.SHA1.hashUnencodedChars(jsonData).toString();

            try {
                if (!Objects.equals(cache.getHash(path), hash) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                        bufferedwriter.write(jsonData);
                    }

                    cache.putNew(path, hash);
                }
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
