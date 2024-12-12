package com.swacky.ohmega.datagen.client.lang;

import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

public class OhmegaEnUsProvider extends LanguageProvider {
    public OhmegaEnUsProvider(PackOutput output) {
        super(output, OhmegaCommon.MODID, "en_us");
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
        add("dataPack." + OhmegaCommon.MODID + ".description", "Mod resources for Ohmega");
        add(OhmegaItems.ANGEL_RING.get());
        add(OhmegaItems.ANGEL_RING.get().getDescriptionId() + ".tooltip", "Allows the wearer to fly");
        add(OhmegaItems.ANGEL_RING.get().getDescriptionId() + ".tooltip.keybind", "Press <BIND> to toggle flight");
        add("accessory_type", "Accessory Type: %s");
        add("accessory_type." + OhmegaCommon.MODID + ".generic", "Generic");
        add("accessory_type." + OhmegaCommon.MODID + ".normal", "Normal");
        add("accessory_type." + OhmegaCommon.MODID + ".utility", "Utility");
        add("accessory_type." + OhmegaCommon.MODID + ".special", "Special");
        add("key.category." + OhmegaCommon.MODID + ".ohmega", "Ohmega");
        add("key." + OhmegaCommon.MODID + ".open_acc_inv", "Open/Close Accessories Inventory");
        add("key." + OhmegaCommon.MODID + ".generic", "Generic %s");
        add("key." + OhmegaCommon.MODID + ".normal", "Normal %s");
        add("key." + OhmegaCommon.MODID + ".utility", "Utility %s");
        add("key." + OhmegaCommon.MODID + ".special", "Special %s");
    }
}
