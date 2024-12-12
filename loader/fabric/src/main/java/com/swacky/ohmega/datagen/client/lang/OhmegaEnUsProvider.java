package com.swacky.ohmega.datagen.client.lang;

import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.OhmegaCommon;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class OhmegaEnUsProvider extends FabricLanguageProvider {
    public OhmegaEnUsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "en_us", lookup);
    }

    private void add(TranslationBuilder builder, Item key) {
        builder.add(key, getName(BuiltInRegistries.ITEM.getKey(key)));
    }

    private static String getName(ResourceLocation registryName) {
        String[] str = registryName.getPath().split("_");
        StringBuilder out = new StringBuilder();
        for (String s : str) {
            out.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        }
        return out.toString().trim();
    }

    @Override
    public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
        builder.add("datapack." + OhmegaCommon.MODID + ".description", "Mod resources for Ohmega");
        add(builder, OhmegaItems.ANGEL_RING);
        builder.add(OhmegaItems.ANGEL_RING.getDescriptionId() + ".tooltip", "Allows the wearer to fly");
        builder.add(OhmegaItems.ANGEL_RING.getDescriptionId() + ".tooltip.keybind", "Press <BIND> to toggle flight");
        builder.add("accessory_type", "Accessory Type: %s");
        builder.add("accessory_type." + OhmegaCommon.MODID + ".generic", "Generic");
        builder.add("accessory_type." + OhmegaCommon.MODID + ".normal", "Normal");
        builder.add("accessory_type." + OhmegaCommon.MODID + ".utility", "Utility");
        builder.add("accessory_type." + OhmegaCommon.MODID + ".special", "Special");
        builder.add("key.category." + OhmegaCommon.MODID + ".ohmega", "Ohmega");
        builder.add("key." + OhmegaCommon.MODID + ".open_acc_inv", "Open/Close Accessories Inventory");
        builder.add("key." + OhmegaCommon.MODID + ".generic", "Generic %s");
        builder.add("key." + OhmegaCommon.MODID + ".normal", "Normal %s");
        builder.add("key." + OhmegaCommon.MODID + ".utility", "Utility %s");
        builder.add("key." + OhmegaCommon.MODID + ".special", "Special %s");
    }
}
