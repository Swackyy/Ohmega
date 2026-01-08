package com.swacky.ohmega.api.datagen.server;

import com.google.gson.JsonObject;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * A simple data generator for {@link AccessoryType}s (server data)
 * <p>
 * Extend this, override {@link #addTypes()} and use {@link #add(String, AccessoryType.Builder)} or equivalent to add your accessory types
 */
public abstract class AccessoryTypeProvider implements DataProvider {
    private final PackOutput output;
    private final String namespace;
    private final Map<String, AccessoryType.Builder> data = new TreeMap<>();

    public AccessoryTypeProvider(PackOutput output, String namespace) {
        this.output = output;
        this.namespace = namespace;
    }

    public abstract void addTypes();

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput output) {
        addTypes();

        if (!data.isEmpty()) {
            JsonObject json = new JsonObject();

            data.forEach((id, type) -> json.add(id, AccessoryType.Serializer.GSON.toJsonTree(type)));
            return DataProvider.saveStable(output, json, this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(namespace).resolve(AccessoryTypeManager.LOCATION));
        }

        return CompletableFuture.allOf();
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
    public void add(Identifier id, AccessoryType.Builder builder) {
        add(id.getPath(), builder);
    }
}
