package com.swacky.ohmega.api.client.model;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

// todo
public final class ModelHelper {
    private static final Identifier ID = Ohmega.id("accessory_layer");

    public static ModelLayerLocation createLocation(Identifier modelId) {
        return new ModelLayerLocation(ID, modelId.toString());
    }
}
