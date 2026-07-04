package com.swacky.ohmega.config;

import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class OhmegaConfig {
    public static final class Client {
        private static final Service IMPL = OhmegaClient.loadService(Service.class);

        public static void bootstrap() {}

        public static Service.Data getData() {
            return IMPL.getData();
        }

        public static boolean isLoaded() {
            return IMPL.isLoaded();
        }

        public static String createPositionDescription(String template, String coordinate, String... args) {
            Object[] combinedArgs = new Object[args.length + 1];
            combinedArgs[0] = coordinate;

            System.arraycopy(args, 0, combinedArgs, 1, args.length);
            return MessageFormat.format(template, combinedArgs);
        }

        public static String createPositionDescription(String template, boolean x, String... args) {
            String axis;

            if (x) {
                axis = "x-coordinate";
            } else {
                axis = "y-coordinate";
            }

            return createPositionDescription(template, axis, args);
        }

        public interface Service {
            String SECTION_EDIT_UI = "edit_ui";
            String SECTION_EDIT_UI_DESCRIPTION = """
                    Contains some configuration values pertaining to the Edit UI""";
            String SECTION_POSITIONS = "positions";
            String SECTION_POSITIONS_DESCRIPTION = """
                    Determines where certain Ohmega elements are placed on different screens""";
            String SECTION_SURVIVAL = "survival";
            String SECTION_SURVIVAL_DESCRIPTION = """
                    Contains positions for the survival inventory""";
            String SECTION_CREATIVE = "creative";
            String SECTION_CREATIVE_DESCRIPTION = """
                    Contains positions for the creative inventory""";
            String SECTION_TOGGLE_EXTENSION_BUTTON = "toggle_extension_button";
            String SECTION_TOGGLE_EXTENSION_BUTTON_DESCRIPTION = """
                    Contains positions for the toggle extension button""";
            String SURVIVAL_INVENTORY = "survival inventory";
            String CREATIVE_INVENTORY = "creative inventory";
            String EXTENSION_DESCRIPTION_TEMPLATE = """
                The {0} of the accessory extension in the {1} menu, relative to the main segment of the current screen""";
            String TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE = """
                The {0} of the toggle extension button in the {1} menu when using the ''{2}'' button style, relative to the main segment of the current screen""";
            String FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE = """
                The {0} of the flip entity button in the {1} menu, relative to the main segment of the current screen""";
            int POSITION_MIN = -2048;
            int POSITION_MAX = 2048;
            // - - -

            String COMPATIBILITY_MODE_KEY = "compatibilityMode";
            String COMPATIBILITY_MODE_DESCRIPTION = """
                    Disables or reworks some useful yet mostly unnoticeable features that may improve mod compatibility in rare cases""";
            boolean COMPATIBILITY_MODE_DEFAULT = false;
            // - - -
            String SHOW_TRANSLATION_TOAST_KEY = "showTranslationToast";
            String SHOW_TRANSLATION_TOAST_DESCRIPTION = """
                    If true, will show a toast referring to Ohmega Crowdin translations on joining a world.
                    This is automatically set to false after the first pop-up, making it only display once""";
            boolean SHOW_TRANSLATION_TOAST_DEFAULT = true;
            // - - -
            String TOGGLE_EXTENSION_BUTTON_STYLE_KEY = "toggleExtensionButtonStyle";
            String TOGGLE_EXTENSION_BUTTON_STYLE_DESCRIPTION = """
                    Style of the accessory extension button
                    DEFAULT: The normal Ohmega button style
                    LEGACY: A Curios/Baubles inspired button that renders next to the inventory player model
                    TAG_LEFT: A small tag-like button appearing just off the top left corner of the inventory
                    TAG_RIGHT: A small tag-like button appearing just off the top right corner of the inventory
                    HIDDEN: Will not show, use the dedicated keybind to open the accessory extension instead""";
            // - - -
            String ACCESSORY_EXTENSION_ID_KEY = "accessoryExtensionId";
            String ACCESSORY_EXTENSION_ID_DESCRIPTION = """
                    The accessory extension type to use, other mods can register custom accessory extensions, which can be chosen here""";
            String ACCESSORY_EXTENSION_ID_DEFAULT = OhmegaClient.DEFAULT_EXTENSION_ID.toString();
            Predicate<Object> ACCESSORY_EXTENSION_ID_VALIDATOR = object -> object instanceof String string && AccessoryExtensions.exists(Identifier.tryParse(string));
            // - - -
            String FILL_DIRECTION_KEY = "fillDirection";
            String FILL_DIRECTION_DESCRIPTION = """
                    The direction that accessory slots will fill up in""";
            FillDirection FILL_DIRECTION_DEFAULT = FillDirection.RIGHT;
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
                    If exceeded, a new column will be made if it does not exceed 'maxColumns'""";
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
            String SHOW_HOVER_TOOLTIP_KEY = "showHoverTooltip";
            String SHOW_HOVER_TOOLTIP_DESCRIPTION = """
                    If true, will display a tooltip box of the type of accessory slot when it is hovered over""";
            boolean SHOW_HOVER_TOOLTIP_DEFAULT = true;
            // - - -
            String BACKGROUND_ALPHA_KEY = "background_alpha";
            String BACKGROUND_ALPHA_DESCRIPTION = """
                    The alpha value for the background of the Edit UI""";
            int BACKGROUND_ALPHA_DEFAULT = 48;
            int BACKGROUND_ALPHA_MIN = 0;
            int BACKGROUND_ALPHA_MAX = 255;
            // - - -
            String MAGNETICS_STRENGTH_KEY = "magneticsStrength";
            String MAGNETICS_STRENGTH_DESCRIPTION = """
                    The maximum pixel distance where magnetic lines will be considered for snapping""";
            int MAGNETICS_STRENGTH_DEFAULT = 5;
            int MAGNETICS_STRENGTH_MIN = 1;
            int MAGNETICS_STRENGTH_MAX = 64;
            // - - -
            String SURVIVAL_EXTENSION_X_KEY = "survivalExtensionX";
            String SURVIVAL_EXTENSION_X_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY);
            int SURVIVAL_EXTENSION_X_DEFAULT = 178;
            // - - -
            String SURVIVAL_EXTENSION_Y_KEY = "survivalExtensionY";
            String SURVIVAL_EXTENSION_Y_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY);
            int SURVIVAL_EXTENSION_Y_DEFAULT = 25;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY = "survivalToggleExtensionButtonDefaultX";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.DEFAULT.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DEFAULT = 132;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY = "survivalToggleExtensionButtonDefaultY";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.DEFAULT.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DEFAULT = 61;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY = "survivalToggleExtensionButtonLegacyX";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.LEGACY.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DEFAULT = 27;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY = "survivalToggleExtensionButtonLegacyY";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.LEGACY.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DEFAULT = 9;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY = "survivalToggleExtensionButtonTagLeftX";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.TAG_LEFT.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DEFAULT = -11;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY = "survivalToggleExtensionButtonTagLeftY";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.TAG_LEFT.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DEFAULT = 8;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY = "survivalToggleExtensionButtonTagRightX";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY, ButtonStyle.TAG_RIGHT.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DEFAULT = 173;
            // - - -
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY = "survivalToggleExtensionButtonTagRightY";
            String SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY, ButtonStyle.TAG_RIGHT.name);
            int SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DEFAULT = 8;
            // - - -
            String SURVIVAL_FLIP_ENTITY_BUTTON_X_KEY = "survivalFlipEntityButtonX";
            String SURVIVAL_FLIP_ENTITY_BUTTON_X_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, true, SURVIVAL_INVENTORY);
            int SURVIVAL_FLIP_ENTITY_BUTTON_X_DEFAULT = 65;
            // - - -
            String SURVIVAL_FLIP_ENTITY_BUTTON_Y_KEY = "survivalFlipEntityButtonY";
            String SURVIVAL_FLIP_ENTITY_BUTTON_Y_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, false, SURVIVAL_INVENTORY);
            int SURVIVAL_FLIP_ENTITY_BUTTON_Y_DEFAULT = 9;
            // - - -
            String CREATIVE_EXTENSION_X_KEY = "creativeExtensionX";
            String CREATIVE_EXTENSION_X_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY);
            int CREATIVE_EXTENSION_X_DEFAULT = 197;
            // - - -
            String CREATIVE_EXTENSION_Y_KEY = "creativeExtensionY";
            String CREATIVE_EXTENSION_Y_DESCRIPTION = createPositionDescription(EXTENSION_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY);
            int CREATIVE_EXTENSION_Y_DEFAULT = 10;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY = "creativeToggleExtensionButtonDefaultX";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.DEFAULT.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DEFAULT = 137;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY = "creativeToggleExtensionButtonDefaultY";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.DEFAULT.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DEFAULT = 19;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY = "creativeToggleExtensionButtonLegacyX";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.LEGACY.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DEFAULT = 74;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY = "creativeToggleExtensionButtonLegacyY";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.LEGACY.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DEFAULT = 7;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY = "creativeToggleExtensionButtonTagLeftX";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.TAG_LEFT.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DEFAULT = -11;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY = "creativeToggleExtensionButtonTagLeftY";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.TAG_LEFT.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DEFAULT = 8;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY = "creativeToggleExtensionButtonTagRightX";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY, ButtonStyle.TAG_RIGHT.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DEFAULT = 192;
            // - - -
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY = "creativeToggleExtensionButtonTagRightY";
            String CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION = createPositionDescription(TOGGLE_EXTENSION_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY, ButtonStyle.TAG_RIGHT.name);
            int CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DEFAULT = 8;
            // - - -
            String CREATIVE_FLIP_ENTITY_BUTTON_X_KEY = "creativeFlipEntityButtonX";
            String CREATIVE_FLIP_ENTITY_BUTTON_X_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, true, CREATIVE_INVENTORY);
            int CREATIVE_FLIP_ENTITY_BUTTON_X_DEFAULT = 95;
            // - - -
            String CREATIVE_FLIP_ENTITY_BUTTON_Y_KEY = "creativeFlipEntityButtonY";
            String CREATIVE_FLIP_ENTITY_BUTTON_Y_DESCRIPTION = createPositionDescription(FLIP_ENTITY_BUTTON_DESCRIPTION_TEMPLATE, false, CREATIVE_INVENTORY);
            int CREATIVE_FLIP_ENTITY_BUTTON_Y_DEFAULT = 7;
            // - - -

            record Data(
                    BooleanLazySavedValue compatibilityMode,
                    BooleanLazySavedValue showTranslationToast,
                    LazySavedValue<ButtonStyle> toggleExtensionButtonStyle,
                    LazySavedValue<String> accessoryExtensionId,
                    LazySavedValue<FillDirection> fillDirection,
                    IntLazySavedValue maxColumns,
                    IntLazySavedValue maxColumnSlots,
                    IntLazySavedValue maxColumnRenderSlots,
                    BooleanLazySavedValue showHoverTooltip,
                    IntLazySavedValue backgroundAlpha,
                    IntLazySavedValue magneticsStrength,
                    IntLazySavedValue survivalExtensionX,
                    IntLazySavedValue survivalExtensionY,
                    IntLazySavedValue survivalToggleExtensionButtonDefaultX,
                    IntLazySavedValue survivalToggleExtensionButtonDefaultY,
                    IntLazySavedValue survivalToggleExtensionButtonLegacyX,
                    IntLazySavedValue survivalToggleExtensionButtonLegacyY,
                    IntLazySavedValue survivalToggleExtensionButtonTagLeftX,
                    IntLazySavedValue survivalToggleExtensionButtonTagLeftY,
                    IntLazySavedValue survivalToggleExtensionButtonTagRightX,
                    IntLazySavedValue survivalToggleExtensionButtonTagRightY,
                    IntLazySavedValue survivalFlipEntityButtonX,
                    IntLazySavedValue survivalFlipEntityButtonY,
                    IntLazySavedValue creativeExtensionX,
                    IntLazySavedValue creativeExtensionY,
                    IntLazySavedValue creativeToggleExtensionButtonDefaultX,
                    IntLazySavedValue creativeToggleExtensionButtonDefaultY,
                    IntLazySavedValue creativeToggleExtensionButtonLegacyX,
                    IntLazySavedValue creativeToggleExtensionButtonLegacyY,
                    IntLazySavedValue creativeToggleExtensionButtonTagLeftX,
                    IntLazySavedValue creativeToggleExtensionButtonTagLeftY,
                    IntLazySavedValue creativeToggleExtensionButtonTagRightX,
                    IntLazySavedValue creativeToggleExtensionButtonTagRightY,
                    IntLazySavedValue creativeFlipEntityButtonX,
                    IntLazySavedValue creativeFlipEntityButtonY) {
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

            Data getData();

            boolean isLoaded();

            enum ButtonStyle {
                DEFAULT("default", 20, 18, true),
                LEGACY("legacy", 9, 9, true),
                TAG_LEFT("tag_left", 14, 8, false),
                TAG_RIGHT("tag_right", 14, 8, false),
                HIDDEN("hidden", 0, 0, false);

                public final String name;
                public final int width;
                public final int height;
                public final Identifier textureLocation;
                public final boolean highlightWhenHovered;

                ButtonStyle(String name, int width, int height, boolean highlightWhenHovered) {
                    this.name = name;
                    this.width = width;
                    this.height = height;
                    this.textureLocation = Ohmega.id("textures/gui/container/accessory_inventory/inventory_buttons/" + name + ".png");
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

        private static @NonNull List<AccessoryType> defaultSlotTypes = List.of();
        private static @NonNull ImmutableSet<AccessoryType> keyboundSlotTypes = ImmutableSet.of();

        public static void bootstrap() {}

        public static Service.Data getData() {
            return IMPL.getData();
        }

        public static boolean isLoaded() {
            return IMPL.isLoaded();
        }

        public static void revalidateCached() {
            Service.Data data = getData();
            List<? extends String> slotTypes = data.defaultSlotTypes().getObject();
            List<AccessoryType> list;

            // todo: respect new config option here

            if (slotTypes != null) {
                int size = slotTypes.size();
                list = new ArrayList<>(size);

                if (data.disableAccessoryTypes().get()) {
                    for (int i = 0; i < size; i++) {
                        list.add(AccessoryType.GENERIC.get());
                    }
                } else {
                    for (String id : slotTypes) {
                        AccessoryType type = AccessoryTypeManager.get(Identifier.parse(id));

                        if (type != AccessoryType.NONE) {
                            list.add(type);
                        }
                    }
                }
            } else {
                list = List.of();
            }

            if (!list.equals(defaultSlotTypes)) {
                defaultSlotTypes = list;

                for (LivingEntity tracker : AccessoryData.DEFAULT_TRACKERS) {
                    OhmegaDataAttachments.getData(tracker).defaultSlots(tracker, EquipContext.CONFIG);
                }
            }

            List<? extends String> types = data.keyboundSlotTypes().getObject();

            if (types != null) {
                ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

                for (String id : types) {
                    builder.add(AccessoryTypeManager.get(Identifier.parse(id)));
                }

                keyboundSlotTypes = builder.build();
            } else {
                keyboundSlotTypes = ImmutableSet.of();
            }
        }

        public static @NonNull List<AccessoryType> getDefaultSlotTypes() {
            return defaultSlotTypes;
        }

        public static @NonNull ImmutableSet<AccessoryType> getKeyboundSlotTypes() {
            return keyboundSlotTypes;
        }

        public interface Service {
            String GENERIC = AccessoryType.GENERIC_ID.toString();
            String NORMAL  = AccessoryType.NORMAL_ID.toString();
            String UTILITY = AccessoryType.UTILITY_ID.toString();
            String SPECIAL = AccessoryType.SPECIAL_ID.toString();
            Predicate<Object> ACCESSORY_TYPE_VALIDATOR = object -> AccessoryTypeManager.getTypes().isEmpty() || (object instanceof String string && AccessoryTypeManager.exists(Identifier.tryParse(string)));
            // - - -

            String DEFAULT_SLOT_TYPES_KEY = "defaultSlotTypes";
            String DEFAULT_SLOT_TYPES_DESCRIPTION = """
                    Defines the types and number of slots to default to for the accessory inventory""";
            List<String> DEFAULT_SLOT_TYPES_DEFAULT = List.of(
                    NORMAL,
                    NORMAL,
                    NORMAL,
                    UTILITY,
                    UTILITY,
                    SPECIAL);
            String DEFAULT_SLOT_TYPES_NEW_VALUE_DEFAULT = NORMAL;
            // - - -
            String SHRINK_DEFAULT_SLOT_TYPES_KEY = "shrinkDefaultSlotTypes";
            String SHRINK_DEFAULT_SLOT_TYPES_DESCRIPTION = """
                    If true, will automatically shrink the default slot types based on registered items' types.
                    This means that if an accessory type exists but no items are tagged with it, all instances of the type will be removed from the default slot list""";
            boolean SHRINK_DEFAULT_SLOT_TYPES_DEFAULT = false;
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
                    DEFAULT: Uses the vanilla 'keepInventory' game-rule
                    ALWAYS_ON: Will never drop accessories on death
                    ALWAYS_OFF: Will always drop accessories on death""";
            // - - -
            String DISABLE_ACCESSORY_TYPES_KEY = "disableAccessoryTypes";
            String DISABLE_ACCESSORY_TYPES_DESCRIPTION = """
                    If true, effectively no accessory types will be used, and they will all be overridden, changing them all to 'ohmega:generic'""";
            boolean DISABLE_ACCESSORY_TYPES_DEFAULT = false;
            // - - -
            String ALLOW_HIDE_ACCESSORIES_KEY = "allowHideAccessories";
            String ALLOW_HIDE_ACCESSORIES_DESCRIPTION = """
                    Will prevent players from toggling visibility on their accessories if false, so that they always render""";
            boolean ALLOW_HIDE_ACCESSORIES_DEFAULT = true;
            // - - -
            record Data(
                    LazySavedValue<List<? extends String>> defaultSlotTypes,
                    BooleanLazySavedValue shrinkDefaultSlotTypes,
                    LazySavedValue<List<? extends String>> keyboundSlotTypes,
                    LazySavedValue<KeepAccessoriesBehaviour> keepAccessoriesBehaviour,
                    BooleanLazySavedValue disableAccessoryTypes,
                    BooleanLazySavedValue allowHideAccessories) {
                public void pull() {
                    defaultSlotTypes.pull();
                    shrinkDefaultSlotTypes.pull();
                    keyboundSlotTypes.pull();
                    keepAccessoriesBehaviour.pull();
                    disableAccessoryTypes.pull();
                    allowHideAccessories.pull();
                    revalidateCached();
                }
            }

            Data getData();


            boolean isLoaded();

            enum KeepAccessoriesBehaviour {
                DEFAULT,
                ALWAYS_ON,
                ALWAYS_OFF
            }
        }
    }
}
