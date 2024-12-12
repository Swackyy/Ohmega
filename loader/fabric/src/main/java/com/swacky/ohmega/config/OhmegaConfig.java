package com.swacky.ohmega.config;

import com.swacky.ohmega.common.init.OhmegaTags;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class OhmegaConfig {
    public static final Client CONFIG_CLIENT;
    public static final ModConfigSpec SPEC_CLIENT;
    public static final Server CONFIG_SERVER;
    public static final ModConfigSpec SPEC_SERVER;

    static {
        Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CONFIG_CLIENT = clientPair.getLeft();
        SPEC_CLIENT = clientPair.getRight();

        Pair<Server, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(Server::new);
        CONFIG_SERVER = serverPair.getLeft();
        SPEC_SERVER = serverPair.getRight();
    }

    public static class Client {
        public ModConfigSpec.BooleanValue compatibilityMode;
        public ModConfigSpec.EnumValue<ButtonStyle> buttonStyle;
        public ModConfigSpec.EnumValue<Side> side;
        public ModConfigSpec.BooleanValue tooltip;
        public ModConfigSpec.IntValue maxColumns;
        public ModConfigSpec.IntValue maxColumnSlots;
        public ModConfigSpec.IntValue maxColumnRenderSlots;

        public Client(ModConfigSpec.Builder builder) {
            builder.comment("Defines client-specific behaviour for the Ohmega mod");

            this.compatibilityMode = builder
                    .comment("\nDisables some useful yet mostly unnoticeable features that may improve mod compatibility in rare cases")
                    .define("compatibilityMode", false);
            this.buttonStyle = builder
                    .comment("\nStyle of the accessory inventory button")
                    .comment("DEFAULT: The normal Ohmega button style")
                    .comment("LEGACY: A curios/baubles inspired button that renders next to the inventory player model")
                    .defineEnum("buttonStyle", ButtonStyle.DEFAULT);
            this.side = builder
                    .comment("\nThe side of the inventory that the accessory inventory will be placed")
                    .defineEnum("side", Side.RIGHT);
            this.tooltip = builder
                    .comment("\nIf true, will display a tooltip box of the type of accessory slot when it is hovered over")
                    .define("showHoverSlotTooltip",  true);
            this.maxColumns = builder
                    //.comment("\nThe maximum columns to render before counting any more as overflow (scrolling)")
                    .comment("\nThe maximum columns to render")
                    .defineInRange("maxColumns", 1, 1, 4);
            this.maxColumnSlots = builder
                    .comment("\nThe maximum amount of slots per column")
                    //.comment("If exceeded, a new column will be made if it does not exceed \"maxColumns\",", "else, a scroller will be created")
                    .comment("If exceeded, a new column will be made if it does not exceed \"maxColumns\"")
                    .defineInRange("maxColumnSlots", 8, 1, 32);
            this.maxColumnRenderSlots = builder
                    .comment("\nThe maximum amount of slots to render per column")
                    .defineInRange("maxColumnRenderSlots", 6, 1, 6);
        }
    }

    public static class Server {
        public ModConfigSpec.ConfigValue<List<? extends String>> slotTypes;
        public ModConfigSpec.ConfigValue<List<? extends String>> keyboundSlotTypes;
        public ModConfigSpec.EnumValue<KeepAccessoriesBehaviour> keepAccessories;
        public ModConfigSpec.BooleanValue noAccessoryTypes;

        public Server(ModConfigSpec.Builder builder) {
            builder.comment("Defines server-specific behaviour for the Ohmega mod");

            String NORMAL = "ohmega:normal";
            String UTILITY = "ohmega:utility";
            String SPECIAL = "ohmega:special";
            this.slotTypes = builder
                    .comment("\nDefines the types of accessories that can be key-bound")
                    .defineList("slotTypes", List.of(NORMAL, NORMAL, NORMAL, UTILITY, UTILITY, SPECIAL), () -> NORMAL, v -> v instanceof String str && OhmegaTags.existsAt(str));
            this.keyboundSlotTypes = builder
                    .comment("\nDefines the types of slot(s) you can have as accessories")
                    .defineListAllowEmpty("keyboundSlotTypes", List.of(UTILITY, SPECIAL), () -> NORMAL, v -> v instanceof String str && OhmegaTags.existsAt(str));
            this.keepAccessories = builder
                    .comment("\nDefines how to handle player death in terms of dropping accessories")
                    .comment("DEFAULT: Uses the vanilla \"keepInventory\" game-rule")
                    .comment("ON: Will never drop accessories on death")
                    .comment("OFF: Will always drop accessories on death")
                    .defineEnum("keepAccessories", KeepAccessoriesBehaviour.DEFAULT);
            this.noAccessoryTypes = builder
                    .comment("\nIf true, effectively no accessory types will be used, and they will all be overridden, changing them all to \"ohmega:generic\" which will not show in-game")
                    .define("noAccessoryTypes", false);
        }
    }

    public enum Side {
        LEFT,
        RIGHT
    }

    @SuppressWarnings("unused")
    public enum ButtonStyle {
        DEFAULT(132, 61, 20, 18, 0, 26, false),
        LEGACY(27, 9, 9, 9, 0, 62, true),
        HIDDEN(0, 0, 0, 0, 0, 0, false);

        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int uOffs;
        private final int vOffs;
        private final boolean shouldUseWidthHovered;

        ButtonStyle(int x, int y, int width, int height, int uOffs, int vOffs, boolean shouldUseWidthHovered) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.uOffs = uOffs;
            this.vOffs = vOffs;
            this.shouldUseWidthHovered = shouldUseWidthHovered;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getUOffs() {
            return this.uOffs;
        }

        public int getVOffs() {
            return this.vOffs;
        }

        public boolean shouldUseWidthHovered() {
            return this.shouldUseWidthHovered;
        }
    }

    public enum KeepAccessoriesBehaviour {
        DEFAULT,
        ON,
        OFF
    }
}