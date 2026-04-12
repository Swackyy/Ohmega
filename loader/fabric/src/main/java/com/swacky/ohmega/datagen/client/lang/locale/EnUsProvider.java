package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.command.ClearCommand;
import com.swacky.ohmega.common.command.InfoCommand;
import com.swacky.ohmega.common.command.ItemCommand;
import com.swacky.ohmega.common.command.ItemsCommand;
import com.swacky.ohmega.common.command.CommandHelper;
import com.swacky.ohmega.common.command.TypeCommand;
import com.swacky.ohmega.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class EnUsProvider extends OhmegaLangProvider {
    public EnUsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "en_us", lookup);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);

        // Datapack
        internalHelper.addDataPackDescription("Mod resources for Ohmega");

        // Toast
        builder.add("toast." + Ohmega.MODID + ".translation.title", "Ohmega translations");
        builder.add("toast." + Ohmega.MODID + ".translation.message", "Consider translating Ohmega on Crowdin through the config menu");

        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Angel Ring",
                "Allows the wearer to fly",
                "Press %s to toggle flight");

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Accessory Type: %s");
        helper.addType(KEY_ACCESSORY_TYPE_NONE, "None");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Generic");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Normal");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Utility");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Special");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%s %s");
        builder.add(KEY_BIND_CATEGORY, "Ohmega");
        builder.add(KEY_BIND_OPEN_ACC_INV, "Open/Close Accessories Inventory");

        // Commands
        // Misc
        builder.add(CommandHelper.CONTEXT_HOVER, "(hover)");
        builder.add(CommandHelper.EXCEPTION_ARGUMENT_LIVING_ONLY, "Only living entities may be specified for this argument, however the provided selector includes non-living entities");
        // Exceptions
        builder.add(AccessoryTypeArgument.EXCEPTION_UNKNOWN_TYPE_KEY, "Unknown accessory type: %s");
        // Clear
        builder.add(ClearCommand.ROOT_EXCEPTION_MULTIPLE, "No matching items were found in %s entities' accessory inventories");
        builder.add(ClearCommand.ROOT_EXCEPTION_SINGLE, "No matching items were found in entity %s's accessory inventory");
        builder.add(ClearCommand.ROOT_FEEDBACK_MULTIPLE, "Removed %s item(s) from %s entities' accessory inventories");
        builder.add(ClearCommand.ROOT_FEEDBACK_SINGLE, "Removed %s item(s) from entity %s's accessory inventory");
        // Info
        builder.add(InfoCommand.CROWDIN_FEEDBACK, "Consider translating Ohmega on Crowdin by clicking this message!");
        builder.add(InfoCommand.DISCORD_FEEDBACK, "If you need help with the API or want to send feedback, click this message to join Ohmega's Discord server");
        builder.add(InfoCommand.REPORT_FEEDBACK, "Thanks for using Ohmega, if you want to report a bug, click this message to open our issue tracker");
        builder.add(InfoCommand.WIKI_FEEDBACK, "Want to make a mod with Ohmega? Click this message to open the Ohmega wiki to learn how");
        // Item
        builder.add(ItemCommand.ROOT_FEEDBACK, "Index %s is out of bounds! Must be greater than or equal to 0 and below %s");
        builder.add(ItemCommand.GET_FEEDBACK, "Entity %s has %s %s in index %s of their accessory inventory");
        builder.add(ItemCommand.SET_FEEDBACK_MULTIPLE, "Set stack in index %s of %s entities' accessory inventories to %s %s");
        builder.add(ItemCommand.SET_FEEDBACK_SINGLE, "Set stack in index %s of entity %s's accessory inventory to %s %s");
        // Items
        builder.add(ItemsCommand.ROOT_FEEDBACK, "Entity %s has the following items in their accessory inventory: %s");
        // Type
        builder.add(TypeCommand.LIST_FEEDBACK, "There are %s accessory type(s) recognised on this world: %s");
        builder.add(TypeCommand.QUERY_FEEDBACK, "Accessory type '%s' has the following properties:%s");

        // Config
        // ConfigurationScreen Forge port UI translations
        // Titles
        internalHelper.addConfigPort("title", "%s Configuration");
        internalHelper.addConfigPortTitle("client", "%s Client Configuration");
        internalHelper.addConfigPortTitle("server", "%s Server Configuration");
        internalHelper.addConfigPortTitle("common", "%s Common Configuration");
        // Types
        internalHelper.addConfigPortType("client", "Client Settings");
        internalHelper.addConfigPortType("server", "Common Settings");
        internalHelper.addConfigPortType("common", "Server Settings");
        // Misc
        internalHelper.addConfigPort("notonline", "Settings in here are determined by the server and cannot be changed while online.");
        internalHelper.addConfigPort("notlan", "Settings in here cannot be edited while your game is open to LAN. Please return to the main menu and load the world again.");
        internalHelper.addConfigPort("notloaded", "Settings in here are only available while a world is loaded.");
        internalHelper.addConfigPort("unsupportedelement", "This value cannot be edited in the UI. Please contact the mod author about providing a custom UI for it.");
        internalHelper.addConfigPort("longstring", "This value is too long to be edited in the UI. Please edit it in the config file.");
        internalHelper.addConfigPort("section", "%s...");
        internalHelper.addConfigPort("sectionedit", "Edit");
        internalHelper.addConfigPort("breadcrumb.order", "%1$s %2$s %3$s");
        internalHelper.addConfigPort("breadcrumb.separator", ">");
        internalHelper.addConfigPort("listelement", "%s:");
        internalHelper.addConfigPort("undo", "Undo");
        internalHelper.addConfigPort("undo.tooltip", "Reverts changes on this screen only.");
        internalHelper.addConfigPort("reset", "Reset");
        internalHelper.addConfigPort("reset.tooltip", "Reverts everything on this screen to its default value.");
        internalHelper.addConfigPort("newlistelement", "+");
        internalHelper.addConfigPort("listelementup", "\u23f6");
        internalHelper.addConfigPort("listelementdown", "\u23f7");
        internalHelper.addConfigPort("listelementremove", "\u274c");
        internalHelper.addConfigPort("rangetooltip", "Range: %s");
        internalHelper.addConfigPort("filenametooltip", "File: \"%s\"");
        internalHelper.addConfigPort("common", "Common Options");
        internalHelper.addConfigPort("client", "Client Options");
        internalHelper.addConfigPort("server", "Server Options");
        internalHelper.addConfigPort("restart.game.title", "Minecraft needs to be restarted");
        internalHelper.addConfigPort("restart.game.text", "One or more of the configuration option that were changed will only take effect when the game is started.");
        internalHelper.addConfigPort("restart.server.title", "World needs to be reloaded");
        internalHelper.addConfigPort("restart.server.text", "One or more of the configuration option that were changed will only take effect when the world is reloaded.");
        internalHelper.addConfigPort("restart.return", "Ignore");
        internalHelper.addConfigPort("restart.return.tooltip", "Your changes will have no effect until you restart!");

        internalHelper.addConfigTitle("Ohmega Config");

        // Client config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Ohmega Client", "Ohmega Client Config");
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
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_TRANSLATION_TOAST_KEY,
                "Show Translation Toast",
                OhmegaConfig.Client.Service.SHOW_TRANSLATION_TOAST_DESCRIPTION);

        // Server config
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Ohmega Server", "Ohmega Server Config");
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
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.ALLOW_HIDE_ACCESSORIES_KEY,
                "Allow Hide Accessories",
                OhmegaConfig.Server.Service.ALLOW_HIDE_ACCESSORIES_DESCRIPTION);
    }
}
