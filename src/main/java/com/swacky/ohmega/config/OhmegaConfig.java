package com.swacky.ohmega.config;

import com.swacky.ohmega.common.core.init.OhmegaTags;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Stream;

public class OhmegaConfig {
    public static final Client CONFIG_CLIENT;
    public static final ForgeConfigSpec SPEC_CLIENT;
    public static final Server CONFIG_SERVER;
    public static final ForgeConfigSpec SPEC_SERVER;

    static {
        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(builder -> build(new Client(), builder));
        CONFIG_CLIENT = clientPair.getLeft();
        SPEC_CLIENT = clientPair.getRight();

        Pair<Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(builder -> build(new Server(), builder));
        CONFIG_SERVER = serverPair.getLeft();
        SPEC_SERVER = serverPair.getRight();
    }

    private static <T extends IOhmegaConfig> T build(T config, ForgeConfigSpec.Builder builder) {
        config.build(builder);
        builder.build();
        return config;
    }

    public static class Client implements IOhmegaConfig {
        public ForgeConfigSpec.BooleanValue compatibilityMode;
        public ForgeConfigSpec.EnumValue<ButtonStyle> buttonStyle;
        public ForgeConfigSpec.EnumValue<Side> side;
        public ForgeConfigSpec.BooleanValue tooltip;
        public ForgeConfigSpec.IntValue maxColumns;
        public ForgeConfigSpec.IntValue maxColumnSlots;
        public ForgeConfigSpec.IntValue maxColumnRenderSlots;

        @Override
        public void build(ForgeConfigSpec.Builder builder) {
            builder.comment("Defines client-specific behaviour for the Ohmega mod");

            compatibilityMode = builder
                    .comment("\nDisables some useful yet mostly unnoticeable features that may improve mod compatibility in rare cases")
                    .define("compatibilityMode", false);
            buttonStyle = builder
                    .comment("\nStyle of the accessory inventory button")
                    .comment("DEFAULT: The normal Ohmega button style")
                    .comment("LEGACY: A curios/baubles inspired button that renders next to the inventory player model")
                    .defineEnum("buttonStyle", ButtonStyle.DEFAULT);
            side = builder
                    .comment("\nThe side of the inventory that the accessory inventory will be placed")
                    .defineEnum("side", Side.RIGHT);
            tooltip = builder
                    .comment("\nIf true, will display a tooltip box of the type of accessory slot when it is hovered over")
                    .define("showHoverSlotTooltip",  true);
            maxColumns = builder
                    //.comment("\nThe maximum columns to render before counting any more as overflow (scrolling)")
                    .comment("\nThe maximum columns to render")
                    .defineInRange("maxColumns", 1, 1, 4);
            maxColumnSlots = builder
                    .comment("\nThe maximum amount of slots per column")
                    //.comment("If exceeded, a new column will be made if it does not exceed \"maxColumns\",", "else, a scroller will be created")
                    .comment("If exceeded, a new column will be made if it does not exceed \"maxColumns\"")
                    .defineInRange("maxColumnSlots", 8, 1, 32);
            maxColumnRenderSlots = builder
                    .comment("\nThe maximum amount of slots to render per column")
                    .defineInRange("maxColumnRenderSlots", 6, 1, 6);
        }
    }

    public static class Server implements IOhmegaConfig {
        public ForgeConfigSpec.ConfigValue<List<? extends String>> slotTypes;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> keyboundSlotTypes;
        public ForgeConfigSpec.EnumValue<KeepAccessoriesBehaviour> keepAccessories;
        public ForgeConfigSpec.BooleanValue noAccessoryTypes;

        @Override
        public void build(ForgeConfigSpec.Builder builder) {
            builder.comment("Defines server-specific behaviour for the Ohmega mod");

            String NORMAL = "ohmega:normal";
            String UTILITY = "ohmega:utility";
            String SPECIAL = "ohmega:special";
            slotTypes = builder
                    .comment("\nDefines the types of accessories that can be key-bound")
                    .defineList("slotTypes", List.of(NORMAL, NORMAL, NORMAL, UTILITY, UTILITY, SPECIAL), v -> v instanceof String str && OhmegaTags.existsAt(str));
            keyboundSlotTypes = builder
                    .comment("\nDefines the types of slot(s) you can have as accessories")
                    .defineListAllowEmpty("keyboundSlotTypes", Stream.of(UTILITY, SPECIAL).toList(), v -> v instanceof String str && OhmegaTags.existsAt(str));
            keepAccessories = builder
                    .comment("\nDefines how to handle player death in terms of dropping accessories")
                    .comment("DEFAULT: Uses the vanilla \"keepInventory\" game-rule")
                    .comment("ON: Will never drop accessories on death")
                    .comment("OFF: Will always drop accessories on death")
                    .defineEnum("keepAccessories", KeepAccessoriesBehaviour.DEFAULT);
            noAccessoryTypes = builder
                    .comment("\nIf true, effectively no accessory types will be used, and they will all be overridden, changing them all to \"ohmega:generic\" which will not show in-game")
                    .define("noAccessoryTypes", false);
        }
    }

    private interface IOhmegaConfig {
        void build(ForgeConfigSpec.Builder builder);
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
