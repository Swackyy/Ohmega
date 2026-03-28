package com.swacky.ohmega.client.model;

import com.swacky.ohmega.mixin.client.ModelLayersMixin;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModelLayerRegistry {
    private static final Map<ModelLayerLocation, LayerDefinition> PROVIDERS = new HashMap<>();

    public static void register(ModelLayerLocation location, Supplier<LayerDefinition> supplier) {
        ModelLayersMixin.getLayers().add(location);

        if (PROVIDERS.putIfAbsent(location, supplier.get()) != null) {
            throw new IllegalArgumentException("Model layer with location '" + location + "' has already been registered");
        }
    }

    public static Map<ModelLayerLocation, LayerDefinition> getProviders() {
        return PROVIDERS;
    }
}
