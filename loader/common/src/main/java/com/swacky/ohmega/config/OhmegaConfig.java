package com.swacky.ohmega.config;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

// Descriptions written in the config file are all in English
public final class OhmegaConfig {
    public static final class Client {
        private static final Service IMPL = OhmegaCommon.loadService(Service.class);

        public static void bootstrap() {}

        public static boolean compatibilityMode() {
            return IMPL.compatibilityMode();
        }

        public static Service.ButtonStyle buttonStyle() {
            return IMPL.buttonStyle();
        }

        public static Service.Side side() {
            return IMPL.side();
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

        public interface Service {
            String COMPATIBILITY_MODE_KEY = "compatibilityMode";
            String COMPATIBILITY_MODE_DESCRIPTION = """
                    Disables some useful yet mostly unnoticeable features that may improve mod compatibility in rare cases""";
            boolean COMPATIBILITY_MODE_DEFAULT = false;
            String BUTTON_STYLE_KEY = "buttonStyle";
            String BUTTON_STYLE_DESCRIPTION = """
                    Style of the accessory inventory button
                    DEFAULT: The normal Ohmega button style
                    LEGACY: A curios/baubles inspired button that renders next to the inventory player model
                    TAG: A small tag-like button appearing just off the top corner of the inventory
                    HIDDEN: Will not show, use the dedicated keybind to open the accessory inventory instead""";
            String INVENTORY_SIDE_KEY = "inventorySide";
            String INVENTORY_SIDE_DESCRIPTION = """
                    The side of the inventory that the accessory inventory will be placed""";
            Side INVENTORY_SIDE_DEFAULT = Side.RIGHT;
            String SHOW_HOVER_TOOLTIP_KEY = "showHoverTooltip";
            String SHOW_HOVER_TOOLTIP_DESCRIPTION = """
                    If true, will display a tooltip box of the type of accessory slot when it is hovered over""";
            boolean SHOW_HOVER_TOOLTIP_DEFAULT = true;
            String MAX_COLUMNS_KEY = "maxColumns";
            String MAX_COLUMNS_DESCRIPTION = """
                    The maximum columns to render""";
            int MAX_COLUMNS_DEFAULT = 1;
            int MAX_COLUMNS_MIN = 1;
            int MAX_COLUMNS_MAX = 4;
            String MAX_COLUMN_SLOTS_KEY = "maxColumnSlots";
            String MAX_COLUMN_SLOTS_DESCRIPTION = """
                    The maximum amount of slots per column
                    If exceeded, a new column will be made if it does not exceed "maxColumns\"""";
            int MAX_COLUMN_SLOTS_DEFAULT = 8;
            int MAX_COLUMN_SLOTS_MIN = 1;
            int MAX_COLUMN_SLOTS_MAX = 32;
            String MAX_COLUMN_RENDER_SLOTS_KEY = "maxColumnRenderSlots";
            String MAX_COLUMN_RENDER_SLOTS_DESCRIPTION = """
                    The maximum amount of slots to render per column""";
            int MAX_COLUMN_RENDER_SLOTS_DEFAULT = 6;
            int MAX_COLUMN_RENDER_SLOTS_MIN = 1;
            int MAX_COLUMN_RENDER_SLOTS_MAX = 6;

            boolean compatibilityMode();

            ButtonStyle buttonStyle();

            Side side();

            boolean showHoverTooltip();

            int maxColumns();

            int maxColumnSlots();

            int maxColumnRenderSlots();

            enum ButtonStyle {
                DEFAULT(new Builder()
                        .both(new Data(132, 61, 20, 18, 0, 26))),
                LEGACY(new Builder()
                        .both(new Data(27, 9, 9, 9, 0, 62))
                        .offsetWidthHighlighted()),
                TAG(new Builder()
                        .left(new Data(-11, 8, 14, 8, 0, 87))
                        .right(new Data(173, 8, 14, 8, 0, 71))
                        .noHoverHighlight()),
                HIDDEN(new Builder()
                        .both(new Data(0, 0, 0, 0, 0, 0)));

                private final Data left;
                private final Data right;
                private final boolean offsetWidthHighlighted;
                private final boolean highlightWhenHovered;

                ButtonStyle(Builder builder) {
                    if (builder.left == null || builder.right == null) {
                        throw new NullPointerException("ButtonStyle builder has not been properly configured. Ensure both left and right data are set");
                    }

                    this.left = builder.left;
                    this.right = builder.right;
                    this.offsetWidthHighlighted = builder.offsetWidthHighlighted;
                    this.highlightWhenHovered = builder.highlightWhenHovered;
                }

                public Data getData() {
                    return Client.side() == Side.LEFT ? left : right;
                }

                public boolean offsetWidthHighlighted() {
                    return offsetWidthHighlighted;
                }

                public boolean highlightWhenHovered() {
                    return highlightWhenHovered;
                }

                public record Data(int x, int y, int width, int height, int u, int v) {}

                private static final class Builder {
                    private Data left;
                    private Data right;
                    private boolean offsetWidthHighlighted;
                    private boolean highlightWhenHovered = true;

                    private Builder() {}

                    private Builder left(Data left) {
                        this.left = left;

                        return this;
                    }

                    private Builder right(Data right) {
                        this.right = right;

                        return this;
                    }

                    private Builder both(Data data) {
                        left = data;
                        right = data;

                        return this;
                    }

                    private Builder offsetWidthHighlighted() {
                        offsetWidthHighlighted = true;

                        return this;
                    }

                    private Builder noHoverHighlight() {
                        highlightWhenHovered = false;

                        return this;
                    }
                }
            }

            enum Side {
                LEFT,
                RIGHT
            }
        }
    }

    public static final class Server {
        private static final Service IMPL = OhmegaCommon.loadService(Service.class);

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

        public interface Service {
            String NORMAL  = AccessoryType.NORMAL_ID.toString();
            String UTILITY = AccessoryType.UTILITY_ID.toString();
            String SPECIAL = AccessoryType.SPECIAL_ID.toString();
            Predicate<Object> ACCESSORY_TYPE_VALIDATOR = v -> v instanceof String str && AccessoryTypeManager.getInstance().exists(str);

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
            String KEYBOUND_SLOT_TYPES_KEY = "keyboundSlotTypes";
            String KEYBOUND_SLOT_TYPES_DESCRIPTION = """
                    Defines the types of accessories that can be key-bound""";
            List<String> KEYBOUND_SLOT_TYPES_DEFAULT = List.of(
                    UTILITY,
                    SPECIAL);
            String KEYBOUND_SLOT_TYPES_NEW_VALUE_DEFAULT = "";
            String KEEP_ACCESSORIES_BEHAVIOUR_KEY = "keepAccessoriesBehaviour";
            String KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION = """
                    Defines how to handle player death in terms of dropping accessories
                    DEFAULT: Uses the vanilla "keepInventory" game-rule
                    ALWAYS_ON: Will never drop accessories on death
                    ALWAYS_OFF: Will always drop accessories on death""";
            String DISABLE_ACCESSORY_TYPES_KEY = "disableAccessoryTypes";
            String DISABLE_ACCESSORY_TYPES_DESCRIPTION = """
                    If true, effectively no accessory types will be used, and they will all be overridden, changing them all to "ohmega:generic\"""";
            boolean DISABLE_ACCESSORY_TYPES_DEFAULT = false;

            List<? extends String> slotTypes();

            Set<? extends String> keyboundSlotTypes();

            KeepAccessoriesBehaviour keepAccessoriesBehaviour();

            boolean disableAccessoryTypes();

            enum KeepAccessoriesBehaviour {
                DEFAULT,
                ALWAYS_ON,
                ALWAYS_OFF
            }
        }
    }
}
