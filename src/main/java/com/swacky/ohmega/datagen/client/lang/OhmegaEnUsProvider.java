package com.swacky.ohmega.datagen.client.lang;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.OhmegaItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

public class OhmegaEnUsProvider extends LanguageProvider {
    public OhmegaEnUsProvider(PackOutput output) {
        super(output, Ohmega.MODID, "en_us");
    }

    private void add(Item key) {
        super.add(key, getName(BuiltInRegistries.ITEM.getKey(key)));
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
    protected void addTranslations() {
        add("dataPack." + Ohmega.MODID + ".description", "Mod resources for Ohmega");
        add(OhmegaItems.ANGEL_RING.get());
        add(OhmegaItems.ANGEL_RING.get().getDescriptionId() + ".tooltip", "Allows the wearer to fly");
        add(OhmegaItems.ANGEL_RING.get().getDescriptionId() + ".tooltip.keybind", "Press <BIND> to toggle flight");
        add("accessory_type", "Accessory Type: %s");
        add("accessory_type." + Ohmega.MODID + ".generic", "Generic");
        add("accessory_type." + Ohmega.MODID + ".normal", "Normal");
        add("accessory_type." + Ohmega.MODID + ".utility", "Utility");
        add("accessory_type." + Ohmega.MODID + ".special", "Special");
        add("key.category." + Ohmega.MODID + ".ohmega", "Ohmega");
        add("key." + Ohmega.MODID + ".open_acc_inv", "Open/Close Accessories Inventory");
        add("key." + Ohmega.MODID + ".generic", "Generic %s");
        add("key." + Ohmega.MODID + ".normal", "Normal %s");
        add("key." + Ohmega.MODID + ".utility", "Utility %s");
        add("key." + Ohmega.MODID + ".special", "Special %s");
    }
}
