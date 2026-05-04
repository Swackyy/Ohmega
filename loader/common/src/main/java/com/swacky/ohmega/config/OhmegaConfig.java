package com.swacky.ohmega.config;

import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

// todo: order elements alphabetically
// Descriptions written in the config file are all in English
public final class OhmegaConfig {
    public static final class Client {
        private static final Service IMPL = OhmegaClient.loadService(Service.class);

        public static void bootstrap() {
        }

        public static boolean compatibilityMode() {
            return IMPL.compatibilityMode();
        }

        public static Service.ButtonStyle buttonStyle() {
            return IMPL.buttonStyle();
        }

        public static Service.FillDirection fillDirection() {
            return IMPL.fillDirection();
        }

        public static boolean showHoverTooltip() {
            return IMPL.showHoverTooltip();
        }

        public static int maxColumns() {
            return IMPL.maxColumns();
        }

        public static int maxColumnSlots() {
            return IMPL.maxColumnSlots();
        }

        public static int maxColumnRenderSlots() {
            return IMPL.maxColumnRenderSlots();
        }

        public static boolean showTranslationToast() {
            return IMPL.showTranslationToast();
        }

        public static void setShowTranslationToast(boolean value) {
            IMPL.setShowTranslationToast(value);
        }

        public static int survivalExtensionX() {
            return IMPL.survivalExtensionX();
        }

        public static int survivalExtensionY() {
            return IMPL.survivalExtensionY();
        }

        public static int creativeExtensionX() {
            return IMPL.creativeExtensionX();
        }

        public static int creativeExtensionY() {
            return IMPL.creativeExtensionY();
        }

        public static boolean isLoaded() {
            return IMPL.isLoaded();
        }

        public interface Service {
            String COMPATIBILITY_MODE_KEY = "compatibilityMode";
            String COMPATIBILITY_MODE_DESCRIPTION = """
                    Disables some useful yet mostly unnoticeable features that may improve mod compatibility in rare cases""";
            boolean COMPATIBILITY_MODE_DEFAULT = false;
            // - - -
            String BUTTON_STYLE_KEY = "buttonStyle";
            String BUTTON_STYLE_DESCRIPTION = """
                    Style of the accessory inventory button
                    DEFAULT: The normal Ohmega button style
                    LEGACY: A curios/baubles inspired button that renders next to the inventory player model
                    TAG_LEFT: A small tag-like button appearing just off the top left corner of the inventory
                    TAG_RIGHT: A small tag-like button appearing just off the top right corner of the inventory
                    HIDDEN: Will not show, use the dedicated keybind to open the accessory inventory instead""";
            // - - -
            String FILL_DIRECTION_KEY = "fillDirection";
            String FILL_DIRECTION_DESCRIPTION = """
                    The direction that accessory slots will fill up in""";
            FillDirection FILL_DIRECTION_DEFAULT = FillDirection.RIGHT;
            // - - -
            String SHOW_HOVER_TOOLTIP_KEY = "showHoverTooltip";
            String SHOW_HOVER_TOOLTIP_DESCRIPTION = """
                    If true, will display a tooltip box of the type of accessory slot when it is hovered over""";
            boolean SHOW_HOVER_TOOLTIP_DEFAULT = true;
            // - - -
            String MAX_COLUMNS_KEY = "maxColumns";
            String MAX_COLUMNS_DESCRIPTION = """
                    The maximum columns to render""";
            int MAX_COLUMNS_DEFAULT = 4;
            int MAX_COLUMNS_MIN = 1;
            int MAX_COLUMNS_MAX = 4;
            // - - -
            String MAX_COLUMN_SLOTS_KEY = "maxColumnSlots";
            String MAX_COLUMN_SLOTS_DESCRIPTION = """
                    The maximum amount of slots per column
                    If exceeded, a new column will be made if it does not exceed "maxColumns\"""";
            int MAX_COLUMN_SLOTS_DEFAULT = 8;
            int MAX_COLUMN_SLOTS_MIN = 1;
            int MAX_COLUMN_SLOTS_MAX = 32;
            // - - -
            String MAX_COLUMN_RENDER_SLOTS_KEY = "maxColumnRenderSlots";
            String MAX_COLUMN_RENDER_SLOTS_DESCRIPTION = """
                    The maximum amount of slots to render per column""";
            int MAX_COLUMN_RENDER_SLOTS_DEFAULT = 6;
            int MAX_COLUMN_RENDER_SLOTS_MIN = 1;
            int MAX_COLUMN_RENDER_SLOTS_MAX = 6;
            // - - -
            String SHOW_TRANSLATION_TOAST_KEY = "showTranslationToast";
            String SHOW_TRANSLATION_TOAST_DESCRIPTION = """
                    If true, will show a toast referring to Ohmega Crowdin translations on joining a world.
                    This is automatically set to false after the first pop-up, making it only display once""";
            boolean SHOW_TRANSLATION_TOAST_DEFAULT = true;
            // - - -
            String SURVIVAL_EXTENSION_X_KEY = "survivalExtensionX";
            String SURVIVAL_EXTENSION_X_DESCRIPTION = """
                    The x-coordinate of the accessory extension in the survival inventory, relative to the main segment of the current screen with left and right being negative and positive X respectively""";
            int SURVIVAL_EXTENSION_X_DEFAULT = 178;
            int SURVIVAL_EXTENSION_X_MIN = -2048;
            int SURVIVAL_EXTENSION_X_MAX = 2048;
            // - - -
            String SURVIVAL_EXTENSION_Y_KEY = "survivalExtensionY";
            String SURVIVAL_EXTENSION_Y_DESCRIPTION = """
                    The y-coordinate of the accessory extension in the survival inventory, relative to the main segment of the current screen with up and down being negative and positive Y respectively""";
            int SURVIVAL_EXTENSION_Y_DEFAULT = 24;
            int SURVIVAL_EXTENSION_Y_MIN = -2048;
            int SURVIVAL_EXTENSION_Y_MAX = 2048;
            // - - -
            String CREATIVE_EXTENSION_X_KEY = "creativeExtensionX";
            String CREATIVE_EXTENSION_X_DESCRIPTION = """
                    The x-coordinate of the accessory extension in the creative inventory, relative to the main segment of the current screen with left and right being negative and positive X respectively""";
            int CREATIVE_EXTENSION_X_DEFAULT = 196;
            int CREATIVE_EXTENSION_X_MIN = -2048;
            int CREATIVE_EXTENSION_X_MAX = 2048;
            // - - -
            String CREATIVE_EXTENSION_Y_KEY = "creativeExtensionY";
            String CREATIVE_EXTENSION_Y_DESCRIPTION = """
                    The y-coordinate of the accessory extension in the creative inventory, relative to the main segment of the current screen with up and down being negative and positive Y respectively""";
            int CREATIVE_EXTENSION_Y_DEFAULT = 8;
            int CREATIVE_EXTENSION_Y_MIN = -2048;
            int CREATIVE_EXTENSION_Y_MAX = 2048;
            // - - -

            boolean compatibilityMode();

            ButtonStyle buttonStyle();

            FillDirection fillDirection();

            boolean showHoverTooltip();

            int maxColumns();

            int maxColumnSlots();

            int maxColumnRenderSlots();

            boolean showTranslationToast();

            void setShowTranslationToast(boolean value);

            int survivalExtensionX();

            int survivalExtensionY();

            int creativeExtensionX();

            int creativeExtensionY();

            boolean isLoaded();

            enum ButtonStyle {
                DEFAULT(20, 18, "default", true),
                LEGACY(9, 9, "legacy", true),
                TAG_LEFT(14, 8, "tag_left", false),
                TAG_RIGHT(14, 8, "tag_right", false),
                HIDDEN(0, 0, null, false);

                public final int width;
                public final int height;
                public final Identifier textureLocation;
                public final boolean highlightWhenHovered;

                ButtonStyle(int width, int height, String textureName, boolean highlightWhenHovered) {
                    this.width = width;
                    this.height = height;
                    this.textureLocation = Ohmega.id("textures/gui/container/accessory_inventory/inventory_buttons/" + textureName + ".png");
                    this.highlightWhenHovered = highlightWhenHovered;
                }
            }

            enum FillDirection {
                LEFT,
                RIGHT
            }
        }
    }

    public static final class Server {
        private static final Service IMPL = Ohmega.loadService(Service.class);

        public static void bootstrap() {}

        @SuppressWarnings("unchecked")
        public static List<String> slotTypes() {
            return (List<String>) IMPL.slotTypes();
        }

        @SuppressWarnings("unchecked")
        public static Set<String> keyboundSlotTypes() {
            return (Set<String>) IMPL.keyboundSlotTypes();
        }

        public static Service.KeepAccessoriesBehaviour keepAccessoriesBehaviour() {
            return IMPL.keepAccessoriesBehaviour();
        }

        public static boolean disableAccessoryTypes() {
            return IMPL.disableAccessoryTypes();
        }

        public static boolean allowHideAccessories() {
            return IMPL.allowHideAccessories();
        }

        public static String menuExtensionId() {
            return IMPL.menuExtensionId();
        }

        public static boolean isLoaded() {
            return IMPL.isLoaded();
        }

        public interface Service {
            String GENERIC  = AccessoryType.GENERIC_ID.toString();
            String NORMAL  = AccessoryType.NORMAL_ID.toString();
            String UTILITY = AccessoryType.UTILITY_ID.toString();
            String SPECIAL = AccessoryType.SPECIAL_ID.toString();
            Predicate<Object> ACCESSORY_TYPE_VALIDATOR = v -> v instanceof String str && AccessoryTypeManager.exists(Identifier.tryParse(str));
            // - - -

            String SLOT_TYPES_KEY = "slotTypes";
            String SLOT_TYPES_DESCRIPTION = """
                    Defines the types and number of slots in the accessory inventory""";
            List<String> SLOT_TYPES_DEFAULT = List.of(
                    NORMAL,
                    NORMAL,
                    NORMAL,
                    UTILITY,
                    UTILITY,
                    SPECIAL);
            String SLOT_TYPES_NEW_VALUE_DEFAULT = NORMAL;
            // - - -
            String KEYBOUND_SLOT_TYPES_KEY = "keyboundSlotTypes";
            String KEYBOUND_SLOT_TYPES_DESCRIPTION = """
                    Defines the types of accessories that can be key-bound""";
            List<String> KEYBOUND_SLOT_TYPES_DEFAULT = List.of(
                    GENERIC,
                    UTILITY,
                    SPECIAL);
            String KEYBOUND_SLOT_TYPES_NEW_VALUE_DEFAULT = "";
            // - - -
            String KEEP_ACCESSORIES_BEHAVIOUR_KEY = "keepAccessoriesBehaviour";
            String KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION = """
                    Defines how to handle player death in terms of dropping accessories
                    DEFAULT: Uses the vanilla "keepInventory" game-rule
                    ALWAYS_ON: Will never drop accessories on death
                    ALWAYS_OFF: Will always drop accessories on death""";
            // - - -
            String DISABLE_ACCESSORY_TYPES_KEY = "disableAccessoryTypes";
            String DISABLE_ACCESSORY_TYPES_DESCRIPTION = """
                    If true, effectively no accessory types will be used, and they will all be overridden, changing them all to "ohmega:generic\"""";
            boolean DISABLE_ACCESSORY_TYPES_DEFAULT = false;
            // - - -
            String ALLOW_HIDE_ACCESSORIES_KEY = "allowHideAccessories";
            String ALLOW_HIDE_ACCESSORIES_DESCRIPTION = """
                    Will prevent players from toggling visibility on their accessories if false, so that they always render""";
            boolean ALLOW_HIDE_ACCESSORIES_DEFAULT = true;
            // - - -
            String MENU_EXTENSION_ID_KEY = "menuExtensionId";
            String MENU_EXTENSION_ID_DESCRIPTION = """
                    The menu extension type to use, other mods can register custom accessory menu extensions, which can be chosen here""";
            String MENU_EXTENSION_ID_DEFAULT = Ohmega.INTERFACE_ID.toString();
            // - - -

            List<? extends String> slotTypes();

            Set<? extends String> keyboundSlotTypes();

            KeepAccessoriesBehaviour keepAccessoriesBehaviour();

            boolean disableAccessoryTypes();

            boolean allowHideAccessories();

            String menuExtensionId();

            boolean isLoaded();

            enum KeepAccessoriesBehaviour {
                DEFAULT,
                ALWAYS_ON,
                ALWAYS_OFF
            }
        }
    }
}
