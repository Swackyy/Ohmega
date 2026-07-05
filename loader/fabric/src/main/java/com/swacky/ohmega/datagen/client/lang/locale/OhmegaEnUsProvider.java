package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.client.command.node.ExtensionsCommand;
import com.swacky.ohmega.client.command.node.InfoCommand;
import com.swacky.ohmega.client.screen.widget.CrowdinButton;
import com.swacky.ohmega.client.screen.widget.FlipEntityButton;
import com.swacky.ohmega.client.screen.widget.ToggleExtensionButton;
import com.swacky.ohmega.api.client.screen.widget.ToggleVisibilityButton;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.command.node.ClearCommand;
import com.swacky.ohmega.common.command.node.ItemCommand;
import com.swacky.ohmega.common.command.node.ItemsCommand;
import com.swacky.ohmega.common.command.node.SlotsCommand;
import com.swacky.ohmega.common.command.node.TypesCommand;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class OhmegaEnUsProvider extends OhmegaLangProvider {
    public OhmegaEnUsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "en_us", lookup);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);
        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Accessory Type: %s");
        helper.addType(KEY_ACCESSORY_TYPE_NONE, "None");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Generic");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Normal");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Utility");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Special");

        // Commands
        // Misc
        builder.add(CommandHelper.CONTEXT_HOVER, "(hover)");
        builder.add(CommandHelper.EXCEPTION_ARGUMENT_LIVING, "Only a living entity may be specified for this argument, however the provided selector returns a non-living entity");
        // Exceptions
        builder.add(AccessoryTypeArgument.EXCEPTION_UNKNOWN_TYPE_KEY, "Unknown accessory type: '%s'");
        builder.add(AccessoryTypeArgument.EXCEPTION_UNSPECIFIABLE_TYPE_KEY, "Accessory type '%s' is marked as non-specifiable, and this argument only accepts specifiable types");
        // Clear
        builder.add(ClearCommand.ROOT_EXCEPTION_MULTIPLE, "No matching items were found in %s entities' accessory inventories");
        builder.add(ClearCommand.ROOT_EXCEPTION_SINGLE, "No matching items were found in entity %s's accessory inventory");
        builder.add(ClearCommand.ROOT_FEEDBACK_MULTIPLE, "Removed %s item(s) from %s entities' accessory inventories");
        builder.add(ClearCommand.ROOT_FEEDBACK_SINGLE, "Removed %s item(s) from entity %s's accessory inventory");
        // Extensions
        builder.add(ExtensionsCommand.ROOT_FEEDBACK, "Ohmega recognises the following %s accessory extension(s): %s");
        // Info
        builder.add(InfoCommand.CROWDIN_FEEDBACK, "Consider translating Ohmega on Crowdin by clicking this message!");
        builder.add(InfoCommand.DISCORD_FEEDBACK, "If you need help with the API or want to send feedback, click this message to join Ohmega's Discord server");
        builder.add(InfoCommand.REPORT_FEEDBACK, "Thanks for using Ohmega, if you want to report a bug, click this message to open our issue tracker");
        builder.add(InfoCommand.WIKI_FEEDBACK, "Want to make a mod with Ohmega? Click this message to open the Ohmega wiki to learn how");
        // Item
        builder.add(ItemCommand.ARGUMENT_INDEX_EXCEPTION, "Index %s is out of bounds! Must be below %s");
        builder.add(ItemCommand.GET_FEEDBACK, "Entity %s has %s in index %s of their accessory inventory");
        builder.add(ItemCommand.SET_FEEDBACK_MULTIPLE, "Set stack in index %s of %s entities' accessory inventories to %s %s");
        builder.add(ItemCommand.SET_FEEDBACK_SINGLE, "Set stack in index %s of entity %s's accessory inventory to %s %s");
        builder.add(ItemCommand.TYPE_GET_FEEDBACK, "Item '%s' has default accessory type '%s'");
        // Items
        builder.add(ItemsCommand.ROOT_FEEDBACK, "Entity %s has the following items in their accessory inventory: %s");
        builder.add(ItemsCommand.ROOT_FEEDBACK_EMPTY, "Entity %s has no items in their accessory inventory");
        // Slots
        builder.add(SlotsCommand.ADD_FEEDBACK_MULTIPLE, "Added %s accessory slot(s) of type '%s' to %s entities' accessory inventories");
        builder.add(SlotsCommand.ADD_FEEDBACK_SINGLE, "Added %s accessory slot(s) of type '%s' to entity %s's accessory inventory");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_MULTIPLE, "No accessory slots were found on %s entities");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_SINGLE, "No accessory slots were found on entity %s");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_MULTIPLE, "Removed %s accessory slot(s) from %s entities");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_SINGLE, "Removed %s accessory slot(s) from entity %s");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_MULTIPLE, "Set %s entities' accessory slots to default");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_SINGLE, "Set entity %s's accessory slots to default");
        builder.add(SlotsCommand.GET_FEEDBACK_RANGED, "Entity %s has %s accessory slots with the following types: %s");
        builder.add(SlotsCommand.GET_FEEDBACK, "Slot with index %s in entity %s's accessory inventory is of type '%s'");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_MULTIPLE, "Inherited entity %s's accessory slots for %s entities");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_MULTIPLE, "Inherited entity %s's accessory slots for %s entities for indexes %s to %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_SINGLE, "Inherited entity %s's accessory slots for entity %s for indexes %s to %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_SINGLE, "Inherited entity %s's accessory slots for entity %s");
        builder.add(SlotsCommand.INSERT_FEEDBACK_MULTIPLE, "Inserted %s accessory slot(s) of type '%s' at index %s in %s entities' accessory inventories");
        builder.add(SlotsCommand.INSERT_FEEDBACK_SINGLE, "Inserted %s accessory slot(s) of type '%s' at index %s in entity %s's accessory inventory");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_MULTIPLE, "Removed %s accessory slot(s) from %s entities");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_SINGLE, "Removed %s accessory slot(s) from entity %s");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_ARGUMENT, "Argument '%s' of value %s is out of bounds, must be greater than or equal to argument '%s'");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_SLOTS, "Argument '%s' of value %s is out of bounds, must be less than or equal to entity %s's number of accessory slots");
        builder.add(SlotsCommand.SET_FEEDBACK_MULTIPLE, "Set accessory slot with index %s in %s entities' accessory inventories to type '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_MULTIPLE, "Set accessory slots with indexes %s to %s in %s entities' accessory inventories to type '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_SINGLE, "Set accessory slots with indexes %s to %s in entity %s's accessory inventory to type '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_SINGLE, "Set accessory slot with index %s in entity %s's accessory inventory to type '%s'");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_DEFAULT, "Entity %s is tracking the default accessory slots");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_NONE, "Entity %s is not tracking any other accessory slots");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_MULTIPLE, "Untracked %s entities' accessory slots");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_SINGLE, "Untracked entity %s's accessory slots");
        // Type
        builder.add(TypesCommand.LIST_FEEDBACK, "There are %s accessory type(s) recognised on this world: %s");
        builder.add(TypesCommand.QUERY_FEEDBACK, "Accessory type '%s' has the following properties: %s");

        // Config
        internalHelper.addConfigTitle("Ohmega Config");

        // Client
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Ohmega Client", "Ohmega Client Config");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Compatibility Mode",
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_TRANSLATION_TOAST_KEY,
                "Show Translation Toast",
                OhmegaConfig.Client.Service.SHOW_TRANSLATION_TOAST_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_KEY,
                "Toggle Extension Button Style",
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.ACCESSORY_EXTENSION_ID_KEY,
                "Accessory Extension ID",
                OhmegaConfig.Client.Service.ACCESSORY_EXTENSION_ID_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Fill direction",
                OhmegaConfig.Client.Service.FILL_DIRECTION_DESCRIPTION);
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
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Show Hover Tooltip",
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_DESCRIPTION);
        // Edit UI
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_EDIT_UI,
                "Edit UI",
                OhmegaConfig.Client.Service.SECTION_EDIT_UI_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_EDIT_UI, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.BACKGROUND_ALPHA_KEY,
                "Background Alpha",
                OhmegaConfig.Client.Service.BACKGROUND_ALPHA_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAGNETICS_STRENGTH_KEY,
                "Magnetics Strength",
                OhmegaConfig.Client.Service.MAGNETICS_STRENGTH_DESCRIPTION);
        // Positions
        // Survival
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_POSITIONS,
                "Positions",
                OhmegaConfig.Client.Service.SECTION_POSITIONS_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_POSITIONS, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON,
                "Toggle Extension Button",
                OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_SURVIVAL,
                "Survival Inventory",
                OhmegaConfig.Client.Service.SECTION_SURVIVAL_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_SURVIVAL, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_X_KEY,
                "Extension X",
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_Y_KEY,
                "Extension Y",
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Toggle Extension Button Default X",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Toggle Extension Button Default Y",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Toggle Extension Button Legacy X",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Toggle Extension Button Legacy Y",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Toggle Extension Button Tag Left X",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Toggle Extension Button Tag Left Y",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Toggle Extension Button Tag Right X",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Toggle Extension Button Tag Right Y",
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_X_KEY,
                "Flip Entity Button X",
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_Y_KEY,
                "Flip Entity Button Y",
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_Y_DESCRIPTION);
        // Creative
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_CREATIVE,
                "Creative Inventory",
                OhmegaConfig.Client.Service.SECTION_CREATIVE_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_CREATIVE, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_X_KEY,
                "Extension X",
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_Y_KEY,
                "Extension Y",
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Toggle Extension Button Default X",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Toggle Extension Button Default Y",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Toggle Extension Button Legacy X",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Toggle Extension Button Legacy Y",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Toggle Extension Button Tag Left X",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Toggle Extension Button Tag Left Y",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Toggle Extension Button Tag Right X",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Toggle Extension Button Tag Right Y",
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_X_KEY,
                "Flip Entity Button X",
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_X_DESCRIPTION);
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_Y_KEY,
                "Flip Entity Button Y",
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_Y_DESCRIPTION);

        // Server
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Ohmega Server", "Ohmega Server Config");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY,
                "Default Slot Types",
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_DESCRIPTION);
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY, "Edit");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.SHRINK_DEFAULT_SLOT_TYPES_KEY,
                "Shrink Default Slot Types",
                OhmegaConfig.Server.Service.SHRINK_DEFAULT_SLOT_TYPES_DESCRIPTION);
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
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.INJECT_VANILLA_CLEAR_KEY,
                "Inject Vanilla Clear",
                OhmegaConfig.Server.Service.INJECT_VANILLA_CLEAR_DESCRIPTION);

        // ConfigurationScreen Forge port UI translations
        // Titles
        internalHelper.addConfigPort("title", "%s Configuration");
        internalHelper.addConfigPortTitle("client", "%s Client Configuration");
        internalHelper.addConfigPortTitle("server", "%s Server Configuration");
        internalHelper.addConfigPortTitle("common", "%s Common Configuration");
        // Types
        internalHelper.addConfigPortType("client", "Client Settings");
        internalHelper.addConfigPortType("server", "Server Settings");
        internalHelper.addConfigPortType("common", "Common Settings");
        // Misc
        internalHelper.addConfigPort("notonline", "Settings in here are determined by the server and cannot be changed while online.");
        internalHelper.addConfigPort("notlan", "Settings in here cannot be edited while your game is open to LAN. Please return to the main menu and load the world again.");
        internalHelper.addConfigPort("notloaded", "Settings in here are only available while a world is loaded.");
        internalHelper.addConfigPort("unsupportedelement", "This value cannot be edited in the UI. Please contact the mod author about providing a custom UI for it.");
        internalHelper.addConfigPort("longstring", "This value is too long to be edited in the UI. Please edit it in the config file.");
        internalHelper.addConfigPort("section", "%s...");
        internalHelper.addConfigPort("sectiontext", "Edit");
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

        // Datapack
        internalHelper.addDataPackDescription("Mod resources for Ohmega");
        internalHelper.addDataPackDescription(OhmegaClient.PACK_DARK_ID, "Dark mode pack for Ohmega");

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Angel Ring",
                "Allows the wearer to fly",
                "Press %s to toggle flight");
        builder.add(Ohmega.MODID + ".item.modifiers.accessory_active", "When active:");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%1$s %2$s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.EDIT_MAGNETICS, "Edit UI Magnetics");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_DOWN, "Edit UI Nudge Down");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_LEFT, "Edit UI Nudge Left");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_RIGHT, "Edit UI Nudge Right");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_UP, "Edit UI Nudge Up");
        internalHelper.add(OhmegaBinds.EDIT_REDO, "Edit UI Redo");
        internalHelper.add(OhmegaBinds.EDIT_SHOW_LINES, "Edit UI Show Distance Lines");
        internalHelper.add(OhmegaBinds.EDIT_UNDO, "Edit UI Undo");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Open/Close Accessory Inventory");
        internalHelper.add(OhmegaBinds.OPEN_EDIT_UI, "Open/Close Extension Edit UI");

        // Toast
        internalHelper.addToast("translation.title", "Ohmega translations");
        internalHelper.addToast("translation.message", "Consider translating Ohmega on Crowdin through the config menu");

        // Widget
        builder.add(CrowdinButton.TRANSLATION_KEY, "Crowdin");
        builder.add(FlipEntityButton.TRANSLATION_KEY, "Flip Entity");
        builder.add(ToggleExtensionButton.TRANSLATION_KEY, "Toggle Extension");
        builder.add(ToggleVisibilityButton.TRANSLATION_KEY, "Toggle Visibility");
    }
}
