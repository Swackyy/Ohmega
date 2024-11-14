package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.OhmegaItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OhmegaItemModelProvider extends ItemModelProvider {
    public OhmegaItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, Ohmega.MODID, helper);
    }

    protected void oneLayerGenerated(Item item, ResourceLocation texture) {
        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(item);
        ResourceLocation itemTexture = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "item/" + texture.getPath());
        if (existingFileHelper.exists(itemTexture, PackType.CLIENT_RESOURCES, ".png", "textures")) {
            getBuilder(loc.getPath()).parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", itemTexture);
        } else {
            System.out.println("Texture for " + loc + " not present at " + itemTexture);
        }
    }

    protected void oneLayerGenerated(Item item) {
        oneLayerGenerated(item, BuiltInRegistries.ITEM.getKey(item));
    }

    @Override
    protected void registerModels() {
        oneLayerGenerated(OhmegaItems.ANGEL_RING.get());
    }
}
