package com.swacky.ohmega.api.client.config;

import com.swacky.ohmega.api.client.OhmegaClient;
import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.text.MessageFormat;
import java.util.function.Predicate;

public final class OhmegaClientConfig {
    private static final @NonNull Service IMPL = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static @NonNull Data getData() {
        return IMPL.getData();
    }

    public static boolean isLoaded() {
        return IMPL.isLoaded();
    }

    public static @NonNull String createPositionDescription(String template, String coordinate, String... args) {
        Object[] combinedArgs = new Object[args.length + 1];
        combinedArgs[0] = coordinate;

        System.arraycopy(args, 0, combinedArgs, 1, args.length);
        // todo: change this
        return MessageFormat.format(template, combinedArgs);
    }

    public static @NonNull String createPositionDescription(String template, boolean x, String... args) {
        String axis;

        if (x) {
            axis = "x-coordinate";
        } else {
            axis = "y-coordinate";
        }

        return createPositionDescription(template, axis, args);
    }

    public record Data(
            @NonNull BooleanLazySavedValue compatibilityMode,
            @NonNull BooleanLazySavedValue showTranslationToast,
            @NonNull LazySavedValue<ButtonStyle> toggleExtensionButtonStyle,
            @NonNull LazySavedValue<String> accessoryExtensionId,
            @NonNull LazySavedValue<FillDirection> fillDirection,
            @NonNull IntLazySavedValue maxColumns,
            @NonNull IntLazySavedValue maxColumnSlots,
            @NonNull IntLazySavedValue maxColumnRenderSlots,
            @NonNull BooleanLazySavedValue showHoverTooltip,
            @NonNull BooleanLazySavedValue renderAccessories,
            @NonNull IntLazySavedValue backgroundAlpha,
            @NonNull IntLazySavedValue magneticsStrength,
            @NonNull IntLazySavedValue survivalExtensionX,
            @NonNull IntLazySavedValue survivalExtensionY,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonDefaultX,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonDefaultY,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonLegacyX,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonLegacyY,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonTagLeftX,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonTagLeftY,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonTagRightX,
            @NonNull IntLazySavedValue survivalToggleExtensionButtonTagRightY,
            @NonNull IntLazySavedValue survivalFlipEntityButtonX,
            @NonNull IntLazySavedValue survivalFlipEntityButtonY,
            @NonNull IntLazySavedValue creativeExtensionX,
            @NonNull IntLazySavedValue creativeExtensionY,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonDefaultX,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonDefaultY,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonLegacyX,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonLegacyY,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonTagLeftX,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonTagLeftY,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonTagRightX,
            @NonNull IntLazySavedValue creativeToggleExtensionButtonTagRightY,
            @NonNull IntLazySavedValue creativeFlipEntityButtonX,
            @NonNull IntLazySavedValue creativeFlipEntityButtonY) {

        public void pull() {
            compatibilityMode.pull();
            showTranslationToast.pull();
            toggleExtensionButtonStyle.pull();
            accessoryExtensionId.pull();
            fillDirection.pull();
            maxColumns.pull();
            maxColumnSlots.pull();
            maxColumnRenderSlots.pull();
            showHoverTooltip.pull();
            renderAccessories.pull();
            backgroundAlpha.pull();
            magneticsStrength.pull();
            survivalExtensionX.pull();
            survivalExtensionY.pull();
            survivalToggleExtensionButtonDefaultX.pull();
            survivalToggleExtensionButtonDefaultY.pull();
            survivalToggleExtensionButtonLegacyX.pull();
            survivalToggleExtensionButtonLegacyY.pull();
            survivalToggleExtensionButtonTagLeftX.pull();
            survivalToggleExtensionButtonTagLeftY.pull();
            survivalToggleExtensionButtonTagRightX.pull();
            survivalToggleExtensionButtonTagRightY.pull();
            survivalFlipEntityButtonX.pull();
            survivalFlipEntityButtonY.pull();
            creativeExtensionX.pull();
            creativeExtensionY.pull();
            creativeToggleExtensionButtonDefaultX.pull();
            creativeToggleExtensionButtonDefaultY.pull();
            creativeToggleExtensionButtonLegacyX.pull();
            creativeToggleExtensionButtonLegacyY.pull();
            creativeToggleExtensionButtonTagLeftX.pull();
            creativeToggleExtensionButtonTagLeftY.pull();
            creativeToggleExtensionButtonTagRightX.pull();
            creativeToggleExtensionButtonTagRightY.pull();
            creativeFlipEntityButtonX.pull();
            creativeFlipEntityButtonY.pull();
        }
    }

    public interface Service {
        @NonNull String SECTION_EDIT_UI = "edit_ui";
        @NonNull String SECTION_EDIT_UI_DESCRIPTION = """
                Contains some configuration values pertaining to the Edit UI""";
        @NonNull String SECTION_POSITIONS = "positions";
        @NonNull String SECTION_POSITIONS_DESCRIPTION = """
                Determines where certain Ohmega elements are placed on different screens""";
        @NonNull String SECTION_SURVIVAL = "survival";
        @NonNull String SECTION_SURVIVAL_DESCRIPTION = """
                Contains positions for the survival inventory""";
        @NonNull String SECTION_CREATIVE = "creative";
        @NonNull String SECTION_CREATIVE_DESCRIPTION = """
                Contains positions for the creative inventory""";
        @NonNull String SECTION_TOGGLE_EXTENSION_BUTTON = "toggle_extension_button";
        @NonNull String SECTION_TOGGLE_EXTENSION_BUTTON_DESCRIPTION = """
                Contains positions for the toggle extension button""";
        @NonNull String SURVIVAL_INVENTORY = "survival inventory";
        @NonNull String CREATIVE_INVENTORY = "creative inventory";
        @NonNull String EXTENSION_DESCRIPTION_TEMPLATE = """
                The {0} of the accessory extension in the {1} menu, relative to the main segment of the current screen""";
        @NonNull String TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE = """
                The {0} of the toggle extension button in the {1} menu when using the ''{2}'' button style, relative to the main segment of the current screen""";
        @NonNull String FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE = """
                The {0} of the flip entity button in the {1} menu, relative to the main segment of the current screen""";
        int POSITION_MIN = -2048;
        int POSITION_MAX = 2048;
        // - - -

        @NonNull String COMPATIBILITY_MODE_KEY = "compatibilityMode";
        @NonNull String COMPATIBILITY_MODE_DESCRIPTION = """
                Disables or reworks some useful yet mostly unnoticeable features that may improve mod compatibility in rare cases""";
        boolean COMPATIBILITY_MODE_DEFAULT = false;
        // - - -
        @NonNull String SHOW_TRANSLATION_TOAST_KEY = "showTranslationToast";
        @NonNull String SHOW_TRANSLATION_TOAST_DESCRIPTION = """
                If true, will show a toast referring to Ohmega Crowdin translations on joining a world.
                This is automatically set to false after the first pop-up, making it only display once""";
        boolean SHOW_TRANSLATION_TOAST_DEFAULT = true;
        // - - -
        @NonNull String TOGGLE_EXTENSION_BUTTON_STYLE_KEY = "toggleExtensionButtonStyle";
        @NonNull String TOGGLE_EXTENSION_BUTTON_STYLE_DESCRIPTION = """
                Style of the accessory extension button
                DEFAULT: The normal Ohmega button style
                LEGACY: A Curios/Baubles inspired button that renders next to the player model in the inventory
                TAG_LEFT: A small tag-like button appearing just off the top left corner of the inventory
                TAG_RIGHT: A small tag-like button appearing just off the top right corner of the inventory
                HIDDEN: Will not show, use the dedicated key-bind to open the accessory extension instead""";
        // - - -
        @NonNull String ACCESSORY_EXTENSION_ID_KEY = "accessoryExtensionId";
        @NonNull String ACCESSORY_EXTENSION_ID_DESCRIPTION = """
                The accessory extension type to use, other mods can register custom accessory extensions, which can be chosen here""";
        @NonNull String ACCESSORY_EXTENSION_ID_DEFAULT = OhmegaClient.DEFAULT_EXTENSION_ID.toString();
        @NonNull Predicate<@NonNull Object> ACCESSORY_EXTENSION_ID_VALIDATOR = object ->
                object instanceof String string && AccessoryExtensions.exists(Identifier.tryParse(string));
        // - - -
        @NonNull String FILL_DIRECTION_KEY = "fillDirection";
        @NonNull String FILL_DIRECTION_DESCRIPTION = """
                The direction that accessory slots will fill up in""";
        @NonNull FillDirection FILL_DIRECTION_DEFAULT = FillDirection.RIGHT;
        // - - -
        @NonNull String MAX_COLUMNS_KEY = "maxColumns";
        @NonNull String MAX_COLUMNS_DESCRIPTION = """
                The maximum columns to render""";
        int MAX_COLUMNS_DEFAULT = 4;
        int MAX_COLUMNS_MIN = 1;
        int MAX_COLUMNS_MAX = 4;
        // - - -
        @NonNull String MAX_COLUMN_SLOTS_KEY = "maxColumnSlots";
        @NonNull String MAX_COLUMN_SLOTS_DESCRIPTION = """
                The maximum amount of slots per column
                If exceeded, a new column will be made if it does not exceed 'maxColumns'""";
        int MAX_COLUMN_SLOTS_DEFAULT = 8;
        int MAX_COLUMN_SLOTS_MIN = 1;
        int MAX_COLUMN_SLOTS_MAX = 32;
        // - - -
        @NonNull String MAX_COLUMN_RENDER_SLOTS_KEY = "maxColumnRenderSlots";
        @NonNull String MAX_COLUMN_RENDER_SLOTS_DESCRIPTION = """
                The maximum amount of slots to render per column""";
        int MAX_COLUMN_RENDER_SLOTS_DEFAULT = 6;
        int MAX_COLUMN_RENDER_SLOTS_MIN = 1;
        int MAX_COLUMN_RENDER_SLOTS_MAX = 6;
        // - - -
        @NonNull String SHOW_HOVER_TOOLTIP_KEY = "showHoverTooltip";
        @NonNull String SHOW_HOVER_TOOLTIP_DESCRIPTION = """
                If true, will display a tooltip box of the type of accessory slot when it is hovered over""";
        boolean SHOW_HOVER_TOOLTIP_DEFAULT = true;
        // - - -
        @NonNull String RENDER_ACCESSORIES_KEY = "renderAccessories";
        @NonNull String RENDER_ACCESSORIES_DESCRIPTION = """
                A global accessory rendering option. If true, will render accessories on entities when applicable, or not at all if false""";
        boolean RENDER_ACCESSORIES_DEFAULT = true;
        // - - -
        @NonNull String BACKGROUND_ALPHA_KEY = "background_alpha";
        @NonNull String BACKGROUND_ALPHA_DESCRIPTION = """
                The alpha value for the background of the Edit UI""";
        int BACKGROUND_ALPHA_DEFAULT = 48;
        int BACKGROUND_ALPHA_MIN = 0;
        int BACKGROUND_ALPHA_MAX = 255;
        // - - -
        @NonNull String MAGNETICS_STRENGTH_KEY = "magneticsStrength";
        @NonNull String MAGNETICS_STRENGTH_DESCRIPTION = """
                The maximum pixel distance where magnetic lines will be considered for snapping""";
        int MAGNETICS_STRENGTH_DEFAULT = 5;
        int MAGNETICS_STRENGTH_MIN = 1;
        int MAGNETICS_STRENGTH_MAX = 64;
        // - - -
        @NonNull String SURVIVAL_EXTENSION_X_KEY = "survivalExtensionX";
        @NonNull String SURVIVAL_EXTENSION_X_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY);
        int SURVIVAL_EXTENSION_X_DEFAULT = 178;
        // - - -
        @NonNull String SURVIVAL_EXTENSION_Y_KEY = "survivalExtensionY";
        @NonNull String SURVIVAL_EXTENSION_Y_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY);
        int SURVIVAL_EXTENSION_Y_DEFAULT = 25;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY = "survivalToggleExtensionButtonDefaultX";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.DEFAULT.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DEFAULT = 132;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY = "survivalToggleExtensionButtonDefaultY";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.DEFAULT.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DEFAULT = 61;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY = "survivalToggleExtensionButtonLegacyX";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.LEGACY.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DEFAULT = 27;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY = "survivalToggleExtensionButtonLegacyY";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.LEGACY.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DEFAULT = 9;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY = "survivalToggleExtensionButtonTagLeftX";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.TAG_LEFT.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DEFAULT = -11;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY = "survivalToggleExtensionButtonTagLeftY";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.TAG_LEFT.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DEFAULT = 8;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY = "survivalToggleExtensionButtonTagRightX";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.TAG_RIGHT.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DEFAULT = 173;
        // - - -
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY = "survivalToggleExtensionButtonTagRightY";
        @NonNull String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.TAG_RIGHT.name);
        int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DEFAULT = 8;
        // - - -
        @NonNull String SURVIVAL_FLIP_ENTITY_BUTTON_X_KEY = "survivalFlipEntityButtonX";
        @NonNull String SURVIVAL_FLIP_ENTITY_BUTTON_X_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY);
        int SURVIVAL_FLIP_ENTITY_BUTTON_X_DEFAULT = 65;
        // - - -
        @NonNull String SURVIVAL_FLIP_ENTITY_BUTTON_Y_KEY = "survivalFlipEntityButtonY";
        @NonNull String SURVIVAL_FLIP_ENTITY_BUTTON_Y_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY);
        int SURVIVAL_FLIP_ENTITY_BUTTON_Y_DEFAULT = 9;
        // - - -
        @NonNull String CREATIVE_EXTENSION_X_KEY = "creativeExtensionX";
        @NonNull String CREATIVE_EXTENSION_X_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY);
        int CREATIVE_EXTENSION_X_DEFAULT = 197;
        // - - -
        @NonNull String CREATIVE_EXTENSION_Y_KEY = "creativeExtensionY";
        @NonNull String CREATIVE_EXTENSION_Y_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY);
        int CREATIVE_EXTENSION_Y_DEFAULT = 10;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY = "creativeToggleExtensionButtonDefaultX";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.DEFAULT.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DEFAULT = 137;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY = "creativeToggleExtensionButtonDefaultY";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.DEFAULT.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DEFAULT = 19;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY = "creativeToggleExtensionButtonLegacyX";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.LEGACY.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DEFAULT = 74;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY = "creativeToggleExtensionButtonLegacyY";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.LEGACY.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DEFAULT = 7;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY = "creativeToggleExtensionButtonTagLeftX";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.TAG_LEFT.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DEFAULT = -11;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY = "creativeToggleExtensionButtonTagLeftY";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.TAG_LEFT.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DEFAULT = 8;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY = "creativeToggleExtensionButtonTagRightX";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.TAG_RIGHT.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DEFAULT = 192;
        // - - -
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY = "creativeToggleExtensionButtonTagRightY";
        @NonNull String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.TAG_RIGHT.name);
        int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DEFAULT = 8;
        // - - -
        @NonNull String CREATIVE_FLIP_ENTITY_BUTTON_X_KEY = "creativeFlipEntityButtonX";
        @NonNull String CREATIVE_FLIP_ENTITY_BUTTON_X_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY);
        int CREATIVE_FLIP_ENTITY_BUTTON_X_DEFAULT = 95;
        // - - -
        @NonNull String CREATIVE_FLIP_ENTITY_BUTTON_Y_KEY = "creativeFlipEntityButtonY";
        @NonNull String CREATIVE_FLIP_ENTITY_BUTTON_Y_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY);
        int CREATIVE_FLIP_ENTITY_BUTTON_Y_DEFAULT = 7;
        // - - -

        @NonNull Data getData();

        boolean isLoaded();
    }
}