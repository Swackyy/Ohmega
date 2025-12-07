package com.swacky.ohmega.datagen.client.lang;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class OhmegaEnUsProvider extends LanguageProvider {
    public OhmegaEnUsProvider(PackOutput output) {
        super(output, OhmegaCommon.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Datapack
        add("dataPack." + OhmegaCommon.MODID + ".description", "Mod resources for Ohmega");

        // Item
        add(OhmegaItems.ANGEL_RING.get(), "Angel Ring");
        add(OhmegaItems.ANGEL_RING.get().getDescriptionId() + ".tooltip", "Allows the wearer to fly");
        add(OhmegaItems.ANGEL_RING.get().getDescriptionId() + ".tooltip.keybind", "Press <BIND> to toggle flight");

        // Accessory type
        add("accessory_type", "Accessory Type: %s");
        add("accessory_type." + OhmegaCommon.MODID + ".generic", "Generic");
        add("tag.item." + OhmegaCommon.MODID + ".generic", "Generic");
        add("accessory_type." + OhmegaCommon.MODID + ".normal", "Normal");
        add("tag.item." + OhmegaCommon.MODID + ".normal", "Normal");
        add("accessory_type." + OhmegaCommon.MODID + ".utility", "Utility");
        add("tag.item." + OhmegaCommon.MODID + ".utility", "Utility");
        add("accessory_type." + OhmegaCommon.MODID + ".special", "Special");
        add("tag.item." + OhmegaCommon.MODID + ".special", "Special");

        // Key-binds
        add("key.category." + OhmegaCommon.MODID + ".ohmega", "Ohmega");
        add("key." + OhmegaCommon.MODID + ".open_acc_inv", "Open/Close Accessories Inventory");
        add("key." + OhmegaCommon.MODID + ".generic", "Generic %s");
        add("key." + OhmegaCommon.MODID + ".normal", "Normal %s");
        add("key." + OhmegaCommon.MODID + ".utility", "Utility %s");
        add("key." + OhmegaCommon.MODID + ".special", "Special %s");

        // Config
        add(OhmegaCommon.MODID + ".configuration.title", "Ohmega config");

        // Client config
        add(OhmegaCommon.MODID + ".configuration.section." + OhmegaCommon.MODID + ".client.toml", "Ohmega Client");
        add(OhmegaCommon.MODID + ".configuration.section." + OhmegaCommon.MODID + ".client.toml.title", "Ohmega Client Config");
        add(OhmegaCommon.MODID + ".configuration.compatibilityMode", "Compatibility Mode");
        add(OhmegaCommon.MODID + ".configuration.compatibilityMode.tooltip", """
                Disables some useful yet mostly unnoticeable features that may improve mod compatibility in rare cases""");
        add(OhmegaCommon.MODID + ".configuration.buttonStyle", "Button Style");
        add(OhmegaCommon.MODID + ".configuration.buttonStyle.tooltip", """
                Style of the accessory inventory button
                DEFAULT: The normal Ohmega button style
                LEGACY: A curios/baubles inspired button that renders next to the inventory player model""");
        add(OhmegaCommon.MODID + ".configuration.side", "Side");
        add(OhmegaCommon.MODID + ".configuration.side.tooltip", """
                The side of the inventory that the accessory inventory will be placed""");
        add(OhmegaCommon.MODID + ".configuration.showHoverSlotTooltip", "Show Hover Slot Tooltip");
        add(OhmegaCommon.MODID + ".configuration.showHoverSlotTooltip.tooltip", """
                If true, will display a tooltip box of the type of accessory slot when it is hovered over""");
        add(OhmegaCommon.MODID + ".configuration.maxColumns", "Max Columns");
        add(OhmegaCommon.MODID + ".configuration.maxColumns.tooltip", """
                The maximum columns to render""");
        add(OhmegaCommon.MODID + ".configuration.maxColumnSlots", "Max Column Slots");
        add(OhmegaCommon.MODID + ".configuration.maxColumnSlots.tooltip", """
                The maximum amount of slots per column
                If exceeded, a new column will be made if it does not exceed "maxColumns\"""");
        add(OhmegaCommon.MODID + ".configuration.maxColumnRenderSlots", "Max Column Render Slots");
        add(OhmegaCommon.MODID + ".configuration.maxColumnRenderSlots.tooltip", """
                The maximum amount of slots to render per column""");

        // Server config
        add(OhmegaCommon.MODID + ".configuration.section." + OhmegaCommon.MODID + ".server.toml", "Ohmega Server");
        add(OhmegaCommon.MODID + ".configuration.section." + OhmegaCommon.MODID + ".server.toml.title", "Ohmega Server Config");
        add(OhmegaCommon.MODID + ".configuration.slotTypes", "Slot Types");
        add(OhmegaCommon.MODID + ".configuration.slotTypes.button", "Edit");
        add(OhmegaCommon.MODID + ".configuration.slotTypes.tooltip", """
                Defines the types of slot(s) you can have as accessories""");
        add(OhmegaCommon.MODID + ".configuration.keyboundSlotTypes", "Key-bound Slot Types");
        add(OhmegaCommon.MODID + ".configuration.keyboundSlotTypes.button", "Edit");
        add(OhmegaCommon.MODID + ".configuration.keyboundSlotTypes.tooltip", """
                Defines the types of accessories that can be key-bound""");
        add(OhmegaCommon.MODID + ".configuration.keepAccessories", "Keep Accessories Behaviour");
        add(OhmegaCommon.MODID + ".configuration.keepAccessories.tooltip", """
                Defines how to handle player death in terms of dropping accessories
                DEFAULT: Uses the vanilla "keepInventory" game-rule
                ON: Will never drop accessories on death
                OFF: Will always drop accessories on death""");
        add(OhmegaCommon.MODID + ".configuration.noAccessoryTypes", "No Accessory Types");
        add(OhmegaCommon.MODID + ".configuration.noAccessoryTypes.tooltip", """
                If true, effectively no accessory types will be used, and they will all be overridden, changing them all to "ohmega:generic" which will not show in-game""");
    }
}
