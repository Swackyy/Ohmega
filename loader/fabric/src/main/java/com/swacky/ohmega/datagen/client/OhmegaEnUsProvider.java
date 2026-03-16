package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfig;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class OhmegaEnUsProvider extends LanguageProvider {
    public OhmegaEnUsProvider(FabricDataGenerator generator) {
        super(generator, "en_us");
    }

    @Override
    public void generateTranslations() {
        InternalLangHelper internalHelper = new InternalLangHelper(this);

        // Datapack
        internalHelper.addDataPackDescription("Mod resources for Ohmega");

        OhmegaLangHelper helper = new OhmegaLangHelper(this::add, OhmegaCommon.MODID);

        // Item
        helper.addKeyboundItem(OhmegaItems.ANGEL_RING,
                "Angel Ring",
                "Allows the wearer to fly",
                "Press %s to toggle flight");

        // Accessory type
        add("accessory_type", "Accessory Type: %s");
        helper.addType("generic", "Generic");
        helper.addType("normal", "Normal");
        helper.addType("utility", "Utility");
        helper.addType("special", "Special");

        // Key-binds (type binds handled in OhmegaLangHelper)
        add("key.ohmega.accessory_type", "%s %s");
        add("key.category." + OhmegaCommon.MODID + '.' + OhmegaCommon.MODID, "Ohmega");
        add("key." + OhmegaCommon.MODID + ".open_acc_inv", "Open/Close Accessories Inventory");

        // Config
        internalHelper.addConfig("title", "Ohmega Config");

        // Client config
        internalHelper.addConfigSection("client.toml", "Ohmega Client", "Ohmega Client Config");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Compatibility Mode",
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.BUTTON_STYLE_KEY,
                "Button Style",
                OhmegaConfig.Client.Service.BUTTON_STYLE_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.INVENTORY_SIDE_KEY,
                "Inventory Side",
                OhmegaConfig.Client.Service.INVENTORY_SIDE_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Show Hover Tooltip",
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Max Columns",
                OhmegaConfig.Client.Service.MAX_COLUMNS_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Max Column Slots",
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Max Column Render Slots",
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_DESCRIPTION);

        // Server config
        internalHelper.addConfigSection("server.toml", "Ohmega Server", "Ohmega Server Config");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.SLOT_TYPES_KEY,
                "Slot Types",
                OhmegaConfig.Server.Service.SLOT_TYPES_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.SLOT_TYPES_KEY, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Key-bound Slot Types",
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Keep Accessories Behaviour",
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Disable Accessory Types",
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_DESCRIPTION);
    }
}
