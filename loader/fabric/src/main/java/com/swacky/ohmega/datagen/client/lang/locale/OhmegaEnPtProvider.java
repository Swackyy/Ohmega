package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.client.screen.widget.ToggleVisibilityButton;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.client.command.node.ExtensionsCommand;
import com.swacky.ohmega.client.command.node.InfoCommand;
import com.swacky.ohmega.client.screen.widget.CrowdinButton;
import com.swacky.ohmega.client.screen.widget.FlipEntityButton;
import com.swacky.ohmega.client.screen.widget.ToggleExtensionButton;
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

public final class OhmegaEnPtProvider extends OhmegaLangProvider {
    private static final String SURVIVAL_INVENTORY = "swashbuckler's loot bag";
    private static final String CREATIVE_INVENTORY = "aimless sailor's loot bag";
    private static final String X_COORDINATE = "latitude";
    private static final String Y_COORDINATE = "longitude";
    private static final String EXTENSION_DESCRIPTION_TEMPLATE = """
            Th' {0} o' th' doubloon chest in th' {1} menu, bearin' t' th' main quart'r o' th' current screen""";
    private static final String TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE = """
            Th' {0} o' th' switch chest press'r in th' {1} menu when using th' ''{2}'' press'r style, bearin' t' th' main quart'r o' th' current screen""";
    private static final String FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE = """
            Th' {0} o' th' flip lubber press'r in th' {1} menu, bearin' t' th' main quart'r o' th' current screen""";

    public OhmegaEnPtProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "en_pt", lookup);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);
        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Type 'yer Doubloon: %s");
        helper.addType(KEY_ACCESSORY_TYPE_NONE, "Em'ty");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "Dull");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Typical");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "Useful");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Shiny");

        // Commands
        // Misc
        builder.add(CommandHelper.CONTEXT_HOVER, "(look)");
        builder.add(CommandHelper.EXCEPTION_ARGUMENT_LIVING, "Only a livin' lubber may be giv'n fer this thing, but th' giv'n selector gives a non-livin' lubber");
        // Exceptions
        builder.add(AccessoryTypeArgument.EXCEPTION_UNKNOWN_TYPE_KEY, "Unknown doubloon type: '%s'");
        builder.add(AccessoryTypeArgument.EXCEPTION_UNSPECIFIABLE_TYPE_KEY, "Doubloon type '%s' be marked as non-specifiable, and this thing only accepts specifiable types");
        // Clear
        builder.add(ClearCommand.ROOT_EXCEPTION_MULTIPLE, "No matching treasures were found in %s lubbers' doubloon chests");
        builder.add(ClearCommand.ROOT_EXCEPTION_SINGLE, "No matching treasures were found in lubber %s's doubloon chest");
        builder.add(ClearCommand.ROOT_FEEDBACK_MULTIPLE, "Took %s treasure(s) from %s lubbers' doubloon chests");
        builder.add(ClearCommand.ROOT_FEEDBACK_SINGLE, "Took %s treasure(s) from lubber %s's doubloon chest");
        // Extensions
        builder.add(ExtensionsCommand.ROOT_FEEDBACK, "Ohmega knows these %s doubloon quart'r(s): %s");
        // Info
        builder.add(InfoCommand.CROWDIN_FEEDBACK, "Consider translating Ohmega on Crowdin by clicking this message!");
        builder.add(InfoCommand.DISCORD_FEEDBACK, "If ye need help with th' API or want t' send messages in bottles, click this message t' board Ohmega's Discord voyage");
        builder.add(InfoCommand.REPORT_FEEDBACK, "Cheers fer using Ohmega, if you want t' report a leak, click this message t' open our issue tracker");
        builder.add(InfoCommand.WIKI_FEEDBACK, "Want t' make a magic jar with Ohmega? Click this message t' open th' Ohmega wiki t' learn how");
        // Item
        builder.add(ItemCommand.ARGUMENT_INDEX_EXCEPTION, "Index %s be off th' ship! Must be and below %s");
        builder.add(ItemCommand.GET_FEEDBACK, "Lubber %s has %s in index %s o' their doubloon chest");
        builder.add(ItemCommand.SET_FEEDBACK_MULTIPLE, "Set stack in index %s o' %s lubbers' doubloon chests t' %s %s");
        builder.add(ItemCommand.SET_FEEDBACK_SINGLE, "Set stack in index %s o' lubber %s's doubloon chest t' %s %s");
        builder.add(ItemCommand.TYPE_GET_FEEDBACK, "Treasure '%s' has default doubloon type '%s'");
        // Items
        builder.add(ItemsCommand.ROOT_FEEDBACK, "Lubber %s has th' following treasures in their doubloon chest: %s");
        builder.add(ItemsCommand.ROOT_FEEDBACK_EMPTY, "Lubber %s has no treasures in their doubloon chest");
        // Slots
        builder.add(SlotsCommand.ADD_FEEDBACK_MULTIPLE, "Added %s doubloon slot(s) o' type '%s' t' %s lubbers' doubloon chests");
        builder.add(SlotsCommand.ADD_FEEDBACK_SINGLE, "Added %s doubloon slot(s) o' type '%s' t' lubber %s's doubloon chest");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_MULTIPLE, "No doubloon slots were found on %s lubbers");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_SINGLE, "No doubloon slots were found on lubber %s");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_MULTIPLE, "Took %s doubloon slot(s) from %s lubbers");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_SINGLE, "Took %s doubloon slot(s) from lubber %s");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_MULTIPLE, "Set %s lubbers' doubloon slots t' default");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_SINGLE, "Set lubber %s's doubloon slots t' default");
        builder.add(SlotsCommand.GET_FEEDBACK_RANGED, "Lubber %s has %s doubloon slots with these types: %s");
        builder.add(SlotsCommand.GET_FEEDBACK, "Slot with index %s in lubber %s's doubloon chest be o' type '%s'");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_MULTIPLE, "Inherited lubber %s's doubloon slots fer %s lubbers");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_MULTIPLE, "Inherited lubber %s's doubloon slots fer %s lubbers fer indexes %s t' %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_SINGLE, "Inherited lubber %s's doubloon slots fer lubber %s fer indexes %s t' %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_SINGLE, "Inherited lubber %s's doubloon slots fer lubber %s");
        builder.add(SlotsCommand.INSERT_FEEDBACK_MULTIPLE, "Inserted %s doubloon slot(s) o' type '%s' at index %s in %s lubbers' doubloon chests");
        builder.add(SlotsCommand.INSERT_FEEDBACK_SINGLE, "Inserted %s doubloon slot(s) o' type '%s' at index %s in lubber %s's doubloon chest");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_MULTIPLE, "Took %s doubloon slot(s) from %s lubbers");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_SINGLE, "Took %s doubloon slot(s) from lubber %s");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_ARGUMENT, "Thing '%s' o' value %s be out o' bounds, must be greater than or equal t' thing '%s'");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_SLOTS, "Thing '%s' o' value %s be out o' bounds, must be less than or equal t' lubber %s's number o' doubloon slots");
        builder.add(SlotsCommand.SET_FEEDBACK_MULTIPLE, "Set doubloon slot with index %s in %s lubbers' doubloon chests t' type '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_MULTIPLE, "Set doubloon slots with indexes %s t' %s in %s lubbers' doubloon chests t' type '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_SINGLE, "Set doubloon slots with indexes %s t' %s in lubber %s's doubloon chest t' type '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_SINGLE, "Set doubloon slot with index %s in lubber %s's doubloon chest t' type '%s'");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_DEFAULT, "Lubber %s be tracking th' default doubloon slots");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_NONE, "Lubber %s be not tracking any other doubloon slots");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_MULTIPLE, "Untracked %s lubbers' doubloon slots");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_SINGLE, "Untracked lubber %s's doubloon slots");
        // Type
        builder.add(TypesCommand.LIST_FEEDBACK, "Thar be %s doubloon type(s) recognised atop this land: %s");
        builder.add(TypesCommand.QUERY_FEEDBACK, "doubloon type '%s' has th' followin' manifest scribbles: %s");

        // Config
        internalHelper.addConfigTitle("Ohmega Ship Logs");

        // Client
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Ohmega Crew", "Ohmega Crew Log");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Compatibility Mode",
                "Disables or reworks some useful yet mostly infrequent occur'nces that might improve yer magic jar compatibility in rare cases");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_TRANSLATION_TOAST_KEY,
                "Fly Translation Flag",
                """
                    If aye, will fly a flag bearin' t' Ohmega Crowdin translations on sailing a sea.
                    Thar be automatically set t' nay after th' first pop-up, making it only fly once""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_KEY,
                "Switch Chest Press'r Style",
                """
                    Style o' th' doubloon chest press'r
                    DEFAULT: Th' typical Ohmega press'r style
                    LEGACY: A Curios/Baubles inspired press'r that draws next t' th' loot bag player model
                    TAG_LEFT: A small tag-like press'r appearing just off th' top left corner o' th' loot bag
                    TAG_RIGHT: A small tag-like press'r appearing just off th' top right corner o' th' loot bag
                    HIDDEN: Will not draw, use th' dedicated keybind t' open th' doubloon chest instead""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.ACCESSORY_EXTENSION_ID_KEY,
                "Doubloon Chest Mark'r",
                """
                    Th' doubloon chest type t' use, other magic jars can register custom doubloon chests, which can be put here""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Fill bearin'",
                """
                    Th' bearin' that doubloon slots will fill up in""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "Most Masts",
                """
                    Th' most masts t' render""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "Most Mast Slots",
                """
                    Th' most number o' slots per mast
                    If buckled, a new mast will be made if it does not be more than 'maxColumns'""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "Most Mast Draw Slots",
                """
                    Th' maximum number o' slots t' draw per mast""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Show Look Scribble",
                """
                    If aye, will show a scribble box o' th' type o' doubloon slot when it is hovered over""");
        // Edit UI
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_EDIT_UI,
                "Draft UI",
                """
                    Contains some ship log scribbles pertaining t' th' Draft UI""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_EDIT_UI, "Steer");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.BACKGROUND_ALPHA_KEY,
                "Background Alpha",
                """
                    Th' alpha value fer th' background o' th' Draft UI""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAGNETICS_STRENGTH_KEY,
                "Spookies Strength",
                """
                    Th' most small box distance where spooky lines will be want'd fer snappin'""");
        // Positions
        // Survival
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_POSITIONS,
                "Positions",
                """
                    Makes where certain Ohmega elements be put on diff'rent screens""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_POSITIONS, "Steer");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON,
                "Switch Chest Press'r",
                """
                    Holds map coordinates fer th' toggle extension press'r""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON, "Steer");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_SURVIVAL,
                "Swashbuckler's Loot Bag",
                """
                    Holds map coordinates fer th' swashbuckler's loot bag""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_SURVIVAL, "Steer");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_X_KEY,
                "Chest Latitude",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_Y_KEY,
                "Chest Longitude",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Switch Chest Press'r Default Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Switch Chest Press'r Default Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Switch Chest Press'r Legacy Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Switch Chest Press'r Legacy Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Switch Chest Press'r Tag Left Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Switch Chest Press'r Tag Left Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Switch Chest Press'r Tag Right Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Switch Chest Press'r Tag Right Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_X_KEY,
                "Flip Lubber Press'r Latitude",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_Y_KEY,
                "Flip Lubber Press'r Longitude",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY));
        // Creative
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_CREATIVE,
                "Aimless Sailor's Loot bag",
                """
                    Holds map coordinates fer th' aimless sailor's loot bag""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_CREATIVE, "Steer");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_X_KEY,
                "Chest Latitude",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_Y_KEY,
                "Chest Longitude",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Switch Chest Press'r Default Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Switch Chest Press'r Default Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Switch Chest Press'r Legacy Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Switch Chest Press'r Legacy Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Switch Chest Press'r Tag Left Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Switch Chest Press'r Tag Left Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Switch Chest Press'r Tag Right Latitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Switch Chest Press'r Tag Right Longitude",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_X_KEY,
                "Flip Lubber Press'r Latitude",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_Y_KEY,
                "Flip Lubber Press'r Longitude",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY));

        // Server
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Ohmega Cap'n", "Ohmega Cap'n's Log");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY,
                "Typical Slot Types",
                """
                    States th' types and number o' slots t' default t' fer th' doubloon loot bag""");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY, "Steer");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.SHRINK_DEFAULT_SLOT_TYPES_KEY,
                "Shrink Typical Slot Types",
                """
                    If aye, will automatically shrink th' default slot types thinkin' on known treasures' types.
                    Thar means that if a doubloon type exists but no treasures be tagged with it, all things o' th' type will be removed from th' default slot list""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Key-bound Slot Types",
                """
                    Defines th' types o' doubloons that can be key-bound""");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Steer");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Keep Doubloons Behaviour",
                """
                    States how t' handle crew death in terms o' dropping doubloons
                    DEFAULT: Uses th' vanilla 'keepInventory' game-rule
                    ALWAYS_ON: Will ne'er drop doubloons on death
                    ALWAYS_OFF: Will always drop doubloons on death""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "Disable Doubloon Types",
                """
                    If aye, there be no doubloon types that will be used, and they will all be overridden by cap'n's orders, changing them all t' 'ohmega:generic'""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.ALLOW_HIDE_ACCESSORIES_KEY,
                "Allow Hide Doubloons",
                """
                    Will prevent crew from hidin' doubloons if nay, so that they always be out""");

        // Datapack
        internalHelper.addDataPackDescription("Magic Jar treasures fer Ohmega");
        internalHelper.addDataPackDescription(OhmegaClient.PACK_DARK_ID, "Eye patch pack fer Ohmega");

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "Davy Jones' Ring",
                "Permits th' bearer t' plunder",
                "Thunk %s t' switch plunder'n");
        builder.add(Ohmega.MODID + ".item.modifiers.accessory_active", "When goin':");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%1$s %2$s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmega");
        internalHelper.add(OhmegaBinds.EDIT_MAGNETICS, "Draft UI Spookies");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_DOWN, "Draft UI Nudge off th' Plank");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_LEFT, "Draft UI Nudge Port");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_RIGHT, "Draft UI Nudge Starboard");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_UP, "Draft UI Nudge t' Heavens");
        internalHelper.add(OhmegaBinds.EDIT_REDO, "Draft UI Rebuckle");
        internalHelper.add(OhmegaBinds.EDIT_SHOW_LINES, "Draft UI Pres'nt Journey Lines");
        internalHelper.add(OhmegaBinds.EDIT_UNDO, "Draft UI Unbuckle");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Open/Shut Doubloon Chest");
        internalHelper.add(OhmegaBinds.OPEN_EDIT_UI, "Open/Shut Draft UI Quart'rs");

        // Toast
        internalHelper.addToast("translation.title", "Ohmega translations");
        internalHelper.addToast("translation.message", "Consider translating Ohmega on Crowdin through th' log menu");

        // Widget
        builder.add(CrowdinButton.TRANSLATION_KEY, "Crowdin");
        builder.add(FlipEntityButton.TRANSLATION_KEY, "Flip Lubber");
        builder.add(ToggleExtensionButton.TRANSLATION_KEY, "Switch Quart'rs");
        builder.add(ToggleVisibilityButton.TRANSLATION_KEY, "Switch Visibility");
    }
}
