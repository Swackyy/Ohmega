package com.swacky.ohmega.datagen.client.lang.locale;

import com.swacky.ohmega.api.client.screen.widget.ToggleVisibilityButton;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.api.common.init.OhmegaItems;
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
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.datagen.client.lang.InternalLangHelper;
import com.swacky.ohmega.datagen.client.lang.OhmegaLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class OhmegaLolUsProvider extends OhmegaLangProvider {
    private static final String SURVIVAL_INVENTORY = "sirvivil inventori";
    private static final String CREATIVE_INVENTORY = "haxxer inventori";
    private static final String X_COORDINATE = "dat way";
    private static final String Y_COORDINATE = "dis way";
    private static final String EXTENSION_DESCRIPTION_TEMPLATE = """
            Teh {0} ov teh kit-cat inventori in teh {1} meneow, home iz teh main bit ov teh current fing""";
    private static final String TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE = """
            Teh {0} ov teh kit-cat box buton in teh {1} meneow when using teh ''{2}'' buton style, home iz teh main bit ov teh current fing""";
    private static final String FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE = """
            Teh {0} ov teh flip cat buton in teh {1} meneow, home iz teh main bit ov teh current fing""";

    public OhmegaLolUsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, "lol_us", lookup);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
        InternalLangHelper internalHelper = new InternalLangHelper(builder);
        OhmegaLangHelper helper = new OhmegaLangHelper(builder::add, Ohmega.MODID);

        // Accessory type
        builder.add(KEY_ACCESSORY_TYPE, "Kit-cat flavr: %s");
        helper.addType(KEY_ACCESSORY_TYPE_NONE, "no");
        helper.addType(KEY_ACCESSORY_TYPE_GENERIC, "borinz");
        helper.addType(KEY_ACCESSORY_TYPE_NORMAL, "Regulr");
        helper.addType(KEY_ACCESSORY_TYPE_UTILITY, "SCRATCHEZ");
        helper.addType(KEY_ACCESSORY_TYPE_SPECIAL, "Sparkel");

        // Commands
        // Misc
        builder.add(CommandHelper.CONTEXT_HOVER, "(meow)");
        builder.add(CommandHelper.EXCEPTION_ARGUMENT_LIVING, "Cat iz ded cat doezn't wan ded cat");
        // Exceptions
        builder.add(AccessoryTypeArgument.EXCEPTION_UNKNOWN_TYPE_KEY, "Cat doezn't know diz kit-cat flavr: '%s'");
        builder.add(AccessoryTypeArgument.EXCEPTION_UNSPECIFIABLE_TYPE_KEY, "Kit-cat flavr '%s' izn't speakablez, diz only want speakablez flavrz");
        // Clear
        builder.add(ClearCommand.ROOT_EXCEPTION_MULTIPLE, "No findz dat in %s catz' kit-cat inventori");
        builder.add(ClearCommand.ROOT_EXCEPTION_SINGLE, "No findz dat in cat %s'z kit-cat inventori");
        builder.add(ClearCommand.ROOT_FEEDBACK_MULTIPLE, "SWIPD %s fing(z) fwom %s catz' kit-cat inventoriz");
        builder.add(ClearCommand.ROOT_FEEDBACK_SINGLE, "SWIPD %s fing(z) from cat %s'z kit-cat inventori");
        // Extensions
        builder.add(ExtensionsCommand.ROOT_FEEDBACK, "Ohmegawd knowz deez %s kit-cat box(z): %s");
        // Info
        builder.add(InfoCommand.CROWDIN_FEEDBACK, "Fink abowt tranzlating Ohmegawd on Crowdin by pokin diz meowsage!");
        builder.add(InfoCommand.DISCORD_FEEDBACK, "If yu needz helpz wif teh API (???) or want to giv feedbak, pok diz meowsage to get in Ohmegawd's Discord servr");
        builder.add(InfoCommand.REPORT_FEEDBACK, "Thankz 4 uzing Ohmegawd, iv yu wantz to report a mous, pok diz meowsage to open owr mous trackerr");
        builder.add(InfoCommand.WIKI_FEEDBACK, "Wantz to make an Ohmegawd fing? Pok diz meowsage to open teh Ohmegawd wiki to learn how");
        // Item
        builder.add(ItemCommand.ARGUMENT_INDEX_EXCEPTION, "Dat %s doezn't work! Haz 2 be below %s");
        builder.add(ItemCommand.GET_FEEDBACK, "Cat %s haz %s in place %s ov der kit-cat inventori");
        builder.add(ItemCommand.SET_FEEDBACK_MULTIPLE, "Maded item in place %s ov %s catz' kit-cat inventoriz to %s %s");
        builder.add(ItemCommand.SET_FEEDBACK_SINGLE, "Maded stack in place %s ov cat %s'z kit-cat inventori to %s %s");
        builder.add(ItemCommand.TYPE_GET_FEEDBACK, "Fing '%s' haz defolt kit-cat flavr '%s'");
        // Items
        builder.add(ItemsCommand.ROOT_FEEDBACK, "Cat %s haz teh following fingz in der kit-cat inventori: %s");
        builder.add(ItemsCommand.ROOT_FEEDBACK_EMPTY, "Cat %s haz no fingz in der kit-cat inventori");
        // Slots
        builder.add(SlotsCommand.ADD_FEEDBACK_MULTIPLE, "Put %s kit-cat slot(z) ov flavr '%s' in %s catz' kit-cat inventoriz");
        builder.add(SlotsCommand.ADD_FEEDBACK_SINGLE, "Put %s kit-cat slot(z) ov flavr '%s' in cat %s'z kit-cat inventori");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_MULTIPLE, "No kit-cat slotz wuz findz on %s catz");
        builder.add(SlotsCommand.CLEAR_EXCEPTION_SINGLE, "No kit-cat slotz wuz findz on cat %s");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_MULTIPLE, "SWIPD %s kit-cat slot(z) from %s catz");
        builder.add(SlotsCommand.CLEAR_FEEDBACK_SINGLE, "SWIPD %s kit-cat slot(z) from cat %s");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_MULTIPLE, "Maded %s catz' kit-cat slotz to defolt");
        builder.add(SlotsCommand.DEFAULT_FEEDBACK_SINGLE, "Maded cat %s'z kit-cat slotz to defolt");
        builder.add(SlotsCommand.GET_FEEDBACK_RANGED, "Cat %s haz %s kit-cat slotz wif deez kit-cat flavrz: %s");
        builder.add(SlotsCommand.GET_FEEDBACK, "Slot wif place %s in cat %s'z kit-cat inventori iz ov flavr '%s'");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_MULTIPLE, "Cat %s'z kit-cat slotz iz now %s othr catz' kit-cat slotz");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_MULTIPLE, "Cat %s'z kit-cat slotz iz now %s other catz' kit-cat slotz fer placez %s to %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_RANGED_SINGLE, "Cat %s'z kit-cat slotz iz now cat %s'z kit-cat slotz fer placez %s to %s");
        builder.add(SlotsCommand.INHERIT_FEEDBACK_SINGLE, "Cat %s'z kit-cat slotz is now cat %s'z kit-cat slotz");
        builder.add(SlotsCommand.INSERT_FEEDBACK_MULTIPLE, "Putted in %s kit-cat slot(z) ov flavr '%s' at place %s in %s catz' kit-cat inventoriz");
        builder.add(SlotsCommand.INSERT_FEEDBACK_SINGLE, "Putted in %s kit-cat slot(z) ov flavr '%s' at place %s in cat %s'z kit-cat inventori");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_MULTIPLE, "SWIPD %s kit-cat slot(z) from %s catz");
        builder.add(SlotsCommand.REMOVE_FEEDBACK_SINGLE, "SWIPD %s kit-cat slot(z) from cat %s");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_ARGUMENT, "Fing '%s' dat iz %s iz out ov bounds, shud be bigr or samez as fing '%s'");
        builder.add(SlotsCommand.ROOT_EXCEPTION_BOUNDS_SLOTS, "Fing '%s' ov dat iz %s iz out ov bounds, shud be smolr or samez as cat %s'z numbr ov kit-cat slotz");
        builder.add(SlotsCommand.SET_FEEDBACK_MULTIPLE, "Maded kit-cat slot wif place %s in %s catz' kit-cat inventoriz to flavr '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_MULTIPLE, "Maded kit-cat slotz wif placez %s to %s in %s catz' kit-cat inventoriz to flavr '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_RANGED_SINGLE, "Maded kit-cat slotz wif placez %s to %s in cat %s'z kit-cat inventori to flavr '%s'");
        builder.add(SlotsCommand.SET_FEEDBACK_SINGLE, "Maded kit-cat slot wif place %s in cat %s'z kit-cat inventori to flavr '%s'");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_DEFAULT, "Cat %s iz spyin' on teh defolt kit-cat slotz");
        builder.add(SlotsCommand.TRACKING_FEEDBACK_NONE, "Cat %s izn't spyin' on any other kit-cat slotz");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_MULTIPLE, "Maded %s catz' kit-cat slotz loneleh");
        builder.add(SlotsCommand.UNTRACK_FEEDBACK_SINGLE, "Maded cat %s'z kit-cat slotz loneleh");
        // Type
        builder.add(TypesCommand.LIST_FEEDBACK, "Der iz %s kit-cat flavr(z) dat we know ov: %s");
        builder.add(TypesCommand.QUERY_FEEDBACK, "Kit-cat flavr '%s' haz deez pwoperteez: %s");

        // Config
        internalHelper.addConfigTitle("Ohmegawd Setinz");

        // Client
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_CLIENT, "Ohmegawd MEEEE", "Ohmegawd MEEEE Setinz");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.COMPATIBILITY_MODE_KEY,
                "Friendz Mode",
                "Kilz or respinz sum gud but mostly not der fingz dat can make bettr yur der fingz sumtimez");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_TRANSLATION_TOAST_KEY,
                "Makez Tranzlation Fing Show",
                """
                    If yiss, will fly a flag bearin' to Ohmegawd Crowdin translations on sailing a sea.
                    Diz be automatically maded to naw after teh first pop-up, so it only show once""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.TOGGLE_EXTENSION_BUTTON_STYLE_KEY,
                "Kit-cat Box Buton Style",
                """
                    Style ov teh kit-cat inventori buton
                    DEFAULT: Teh bestst Ohmegawd buton style
                    LEGACY: A Curios/Baubles inspird buton dat apeerz next to teh cat model in teh inventori
                    TAG_LEFT: A smol tag-lik buton dat iz appearin' just off teh top left corner ov teh inventori
                    TAG_RIGHT: A smol tag-like buton appearing just off teh top right corner ov teh inventori
                    HIDDEN: Itz not ther""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.ACCESSORY_EXTENSION_ID_KEY,
                "Kit-cat Box Flavr",
                """
                    Teh kit-cat inventori flavr to use, other fings can add der own kit-cat inventoriz, which can be putz here""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.FILL_DIRECTION_KEY,
                "Putter Direcshon",
                """
                    Teh direcshon dat kit-cat slotz will be fillingz up in""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMNS_KEY,
                "MOST SCRATCHEZPOSTZ",
                """
                    TEH MOST SCRATCHEZPOSTZ TO HAV""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_SLOTS_KEY,
                "MOST SCRATCHEZPOSTZ SLOTZ",
                """
                    TEH MOST NUMBR OF SLOTZ FER SCRATCHEZPOSTZ
                    If scratchd out, a new SCRATCHEZPOST will be made iv it doezn't iz more den 'maxColumns'""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAX_COLUMN_RENDER_SLOTS_KEY,
                "MOST SCRATCHEZPOSTZ SEEIN' SLOTZ",
                """
                    TEH MOST NUMBR OF SLOTZ YU SEE FER A SCRATCHEZPOST""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SHOW_HOVER_TOOLTIP_KEY,
                "Show Meower",
                """
                    If yiss, showz a meower box ov teh flavr ov kit-cat slot when it is meowed on""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.RENDER_ACCESSORIES_KEY,
                "Show Kit-catz",
                """
                    If yiss, showz kit-catz on catz wen wantz, never wantz if naw""");
        // Edit UI
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_EDIT_UI,
                "Kit-cat Box Pokinz",
                """
                    Haz sum Kit-cat box pokin' setinz""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_EDIT_UI, "Claw");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.BACKGROUND_ALPHA_KEY,
                "Backgrownd Alfa",
                """
                    Teh alfaness fer teh backgrownd ov teh Kit-cat box pokin'""");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.MAGNETICS_STRENGTH_KEY,
                "Fridj Strength",
                """
                    How much cat needz to poke at a fridj fing to nock it off""");
        // Positions
        // Survival
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_POSITIONS,
                "Placez",
                """
                    Makes wher sum Ohmegawd fings be putted in placez""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_POSITIONS, "Claw");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON,
                "Kit-cat Box Button",
                """
                    Haz positionz fer teh kit-cat box buton""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_TOGGLE_EXTENSION_BUTTON, "Claw");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_SURVIVAL,
                "Sirvivil Inventori",
                """
                    Haz positionz fer teh sirvivil inventori""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_SURVIVAL, "Claw");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_X_KEY,
                "Box Dat Way",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_EXTENSION_Y_KEY,
                "Box Dis Way",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Kit-cat Box Buton Default Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Kit-cat Box Buton Default Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Kit-cat Box Buton Legacy Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Kit-cat Box Buton Legacy Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Kit-cat Box Buton Tag Left Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Kit-cat Box Buton Tag Left Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Kit-cat Box Buton Tag Right Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Kit-cat Box Buton Tag Right Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_X_KEY,
                "Flip Cat Buton Dat Way",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, SURVIVAL_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SURVIVAL_FLIP_ENTITY_BUTTON_Y_KEY,
                "Flip Cat Buton Dis Way",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, SURVIVAL_INVENTORY));
        // Creative
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.SECTION_CREATIVE,
                "Haxxer Inventori",
                """
                    Haz positionz fer teh haxxer inventori""");
        internalHelper.addConfigButton(OhmegaConfig.Client.Service.SECTION_CREATIVE, "Claw");
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_X_KEY,
                "Box Dat Way",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_EXTENSION_Y_KEY,
                "Box Dis Way",
                OhmegaConfig.Client.createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY,
                "Kit-cat Box Buton Default Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY,
                "Kit-cat Box Buton Default Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.DEFAULT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY,
                "Kit-cat Box Buton Legacy Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY,
                "Kit-cat Box Buton Legacy Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.LEGACY.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY,
                "Kit-cat Box Buton Tag Left Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY,
                "Kit-cat Box Buton Tag Left Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_LEFT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY,
                "Kit-cat Box Buton Tag Right Dat Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY,
                "Kit-cat Box Buton Tag Right Dis Way",
                OhmegaConfig.Client.createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY, OhmegaConfig.Client.Service.ButtonStyle.TAG_RIGHT.name));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_X_KEY,
                "Flip Cat Buton Dat Way",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, X_COORDINATE, CREATIVE_INVENTORY));
        internalHelper.addConfigOption(
                OhmegaConfig.Client.Service.CREATIVE_FLIP_ENTITY_BUTTON_Y_KEY,
                "Flip Cat Buton Dis Way",
                OhmegaConfig.Client.createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, Y_COORDINATE, CREATIVE_INVENTORY));

        // Server
        internalHelper.addConfigSection(KEY_CONFIG_SECTION_SERVER, "Ohmegawd Friendz", "Ohmegawd Friendz Setinz");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY,
                "Defolt Slot Flavrz",
                """
                    Makez teh flavrz and numbr ov slotz to defolt to fer teh kit-cat inventori""");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.DEFAULT_SLOT_TYPES_KEY, "Claw");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.SHRINK_DEFAULT_SLOT_TYPES_KEY,
                "Shrink Typical Slot Types",
                """
                    If yiss, makez teh defolt slot flavrz be smolr smartzly.
                    Diss meanz dat iv a kit-cat flavr be aliv but no fingz be tagged wif it, all fingz ov teh flavr will be killd from teh defolt slotz""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY,
                "Kee-bound Slot Kit-cat Flavrz",
                """
                    Defines teh flavrz ov kit-catz dat can be kee blinded""");
        internalHelper.addConfigButton(OhmegaConfig.Server.Service.KEYBOUND_SLOT_TYPES_KEY, "Claw");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.KEEP_ACCESSORIES_BEHAVIOUR_KEY,
                "Keep Kit-catz Behaviour",
                """
                    Makez how kit-catz drop from ded catz
                    DEFAULT: Uses teh vanilla 'keepInventory' gemrul
                    ALWAYS_ON: No drop kit-catz on dieding
                    ALWAYS_OFF: Drop kit-catz on dieding ALL TEIM""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.DISABLE_ACCESSORY_TYPES_KEY,
                "No Kit-cat Flavrz",
                """
                    If yiss, there be no kit-cat flavrz dat will be used, and dey will all be VERY BLAND az 'ohmega:generic' flavr""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.ALLOW_HIDE_ACCESSORIES_KEY,
                "Make Alow Hide Kit-catz",
                """
                    If naw will make catz no abl to hide kit-catz""");
        internalHelper.addConfigOption(
                OhmegaConfig.Server.Service.INJECT_VANILLA_CLEAR_KEY,
                "SCRATCH Vanilla Clear",
                """
                    Scratchez teh kit-cat clearingz to teh vanilla inventori clearingz""");

        // Datapack
        internalHelper.addDataPackDescription("Fing fingz fer Ohmegawd");
        internalHelper.addDataPackDescription(OhmegaClient.PACK_DARK_ID, "Tabi cat mod fer Ohmegawd");

        // Item
        helper.addKeyboundItem(OhmegaItems.getAngelRing(),
                "GOD CAT RING",
                "Makes teh cat go wheeeeeeee",
                "Tap %s to yiss or naw wheeeeeeee");
        builder.add(Ohmega.MODID + ".item.modifiers.accessory_active", "Wen meow:");

        // Key-binds (type binds handled in OhmegaLangHelper)
        builder.add(KEY_BIND_ACCESSORY_TYPE, "%1$s %2$s");
        internalHelper.add(OhmegaBinds.CATEGORY, "Ohmegawd");
        internalHelper.add(OhmegaBinds.EDIT_MAGNETICS, "Kit-cat Box Pokinz Fridj");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_DOWN, "Kit-cat Box Pokinz Nudge Less Dis Way");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_LEFT, "Kit-cat Box Pokinz Nudge Less Dat Way");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_RIGHT, "Kit-cat Box Pokinz Nudge More Dat Way");
        internalHelper.add(OhmegaBinds.EDIT_NUDGE_UP, "Kit-cat Box Pokinz Nudge More Dis Way");
        internalHelper.add(OhmegaBinds.EDIT_REDO, "Kit-cat Box Pokinz Repoke");
        internalHelper.add(OhmegaBinds.EDIT_SHOW_LINES, "Kit-cat Box Pokinz Show Stringz");
        internalHelper.add(OhmegaBinds.EDIT_UNDO, "Kit-cat Box Pokinz Unpoke");
        internalHelper.add(OhmegaBinds.OPEN_ACCESSORY_INVENTORY, "Yiss/Naw Kit-cat Box");
        internalHelper.add(OhmegaBinds.OPEN_EDIT_UI, "Yiss/Naw Kit-cat Box Pokinz");

        // Toast
        internalHelper.addToast("translation.title", "Ohmegawd tranzlatingz");
        internalHelper.addToast("translation.message", "Fink abowt tranzlatingz Ohmegawd on Crowdinz frew teh Ohmegawd setinz meneow");

        // Widget
        builder.add(CrowdinButton.TRANSLATION_KEY, "Crowdinz");
        builder.add(FlipEntityButton.TRANSLATION_KEY, "Flip Cat");
        builder.add(ToggleExtensionButton.TRANSLATION_KEY, "Togl Kit-cat Box");
        builder.add(ToggleVisibilityButton.TRANSLATION_KEY, "Togl Yiss See No See");
    }
}
