package com.swacky.ohmega.api.datagen.server;

import com.google.gson.JsonObject;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
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
    private final @NonNull PackOutput output;
    private final @NonNull String namespace;
    private final @NonNull Map<String, AccessoryType.Builder> data = new TreeMap<>();

    public AccessoryTypeProvider(@NonNull PackOutput output, @NonNull String namespace) {
        this.output = output;
        this.namespace = namespace;
    }

    /**
     * Override and call {@link #add(String, AccessoryType.Builder)}
     */
    protected abstract void addTypes();

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cache) {
        addTypes();

        if (!data.isEmpty()) {
            JsonObject json = new JsonObject();

            data.forEach((id, type) -> json.add(id, AccessoryType.Serializer.GSON.toJsonTree(type)));
            return DataProvider.saveStable(cache, json, output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(namespace).resolve(AccessoryTypeManager.LOCATION));
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
    protected void add(@NonNull String name, AccessoryType.@NonNull Builder builder) {
        data.put(name, builder);
    }

    /**
     * Same as the above method, does not differentiate by namespace. If you wish to add accessory types for another namespace,
     * use another instance of {@link AccessoryTypeProvider} passing in a different namespace to the {@code super}
     */
    protected void add(@NonNull Identifier id, AccessoryType.@NonNull Builder builder) {
        add(id.getPath(), builder);
    }
}
