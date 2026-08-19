package com.swacky.ohmega.api.client.model;

import com.swacky.ohmega.api.mixin.client.ModelLayersMixinAccessor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModelLayerRegistry {
    private static final Map<ModelLayerLocation, LayerDefinition> PROVIDERS = new IdentityHashMap<>();

    public static void register(ModelLayerLocation location, Supplier<LayerDefinition> supplier) {
        ModelLayersMixinAccessor.getLayers().add(location);

        if (PROVIDERS.putIfAbsent(location, supplier.get()) != null) {
            throw new IllegalArgumentException("Model layer with location '" + location + "' has already been registered");
        }
    }

    public static Map<ModelLayerLocation, LayerDefinition> getProviders() {
        return PROVIDERS;
    }
}
