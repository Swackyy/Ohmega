package com.swacky.ohmega.api.datagen.server;

import com.google.gson.JsonObject;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.swacky.ohmega.common.accessorytype.AccessoryType.Deserializer.*;

/**
 * A simple data generator for {@link AccessoryType}s (server data)
 * <p>
 * Extend this, override {@link #addTypes()} and use {@link #add(String, AccessoryType.Builder)} or equivalent to add your accessory types
 */
public abstract class AccessoryTypeProvider implements DataProvider {
    private final DataGenerator generator;
    private final String namespace;
    private final Map<String, AccessoryType.Builder> data = new TreeMap<>();

    public AccessoryTypeProvider(DataGenerator generator, String namespace) {
        this.generator = generator;
        this.namespace = namespace;
    }

    public abstract void addTypes();

    @Override
    public void run(@NonNull HashCache cache) {
        addTypes();

        if (!data.isEmpty()) {
            JsonObject json = new JsonObject();

            data.forEach((id, type) -> json.add(id, AccessoryType.Serializer.GSON.toJsonTree(type)));

            Path path = generator.getOutputFolder()
                    .resolve("data")
                    .resolve(namespace)
                    .resolve(AccessoryTypeManager.LOCATION);
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
                throw new RuntimeException("Could not write accessory type data for generator with namespace '" + namespace + '\'', e);
            }
        }
    }

    @Override
    public @NonNull String getName() {
        return "Accessory Types for namespace '" + namespace + '\'';
    }

    /**
     * Add a new accessory type to be generated
     * @param name name of this type, e.g: "normal", "utility"
     * @param builder contains the data pertaining to the accessory type
     */
    public void add(String name, AccessoryType.Builder builder) {
        data.put(name, builder);
    }

    /**
     * Same as the above method, does not differentiate by namespace. If you wish to add accessory types for another namespace,
     * use another instance of {@link AccessoryTypeProvider} passing in a different namespace to the {@code super}
     */
    public void add(ResourceLocation id, AccessoryType.Builder builder) {
        add(id.getPath(), builder);
    }
}
