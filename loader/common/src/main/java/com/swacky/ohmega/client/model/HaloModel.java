package com.swacky.ohmega.client.model;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public final class HaloModel extends Model<LivingEntityRenderState> {
    public static final ModelLayerLocation LOCATION = new ModelLayerLocation(Ohmega.id("halo"), "main");

    public HaloModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);
    }

    public static LayerDefinition createDefinition() {
        MeshDefinition mesh = new MeshDefinition();

        mesh.getRoot().addOrReplaceChild("model", CubeListBuilder.create()
                        .texOffs(0, 2).addBox(-3, -1, -3, 6, 1, 1)
                        .texOffs(6, 5).addBox(-3, -1, -2, 1, 1, 4)
                        .texOffs(0, 0).addBox(-3, -1, 2, 6, 1, 1)
                        .texOffs(0, 4).addBox(2, -1, -2, 1, 1, 4),
                PartPose.offset(0, 0, 0));
        return LayerDefinition.create(mesh, 16, 16);
    }
}
