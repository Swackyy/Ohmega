package com.swacky.ohmega.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OhmegaConfigImpl {
    public static final class Client implements OhmegaConfig.Client.Service {
        private static ModConfigSpec spec;

        public ModConfigSpec.BooleanValue compatibilityMode;
        public ModConfigSpec.EnumValue<ButtonStyle> buttonStyle;
        public ModConfigSpec.EnumValue<Side> side;
        public ModConfigSpec.BooleanValue showHoverTooltip;
        public ModConfigSpec.IntValue maxColumns;
        public ModConfigSpec.IntValue maxColumnSlots;
        public ModConfigSpec.IntValue maxColumnRenderSlots;

        public Client() {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            compatibilityMode = builder
                .comment(COMPATIBILITY_MODE_DESCRIPTION)
                .define(COMPATIBILITY_MODE_KEY, COMPATIBILITY_MODE_DEFAULT);
            buttonStyle = builder
                .comment(BUTTON_STYLE_DESCRIPTION)
                .defineEnum(BUTTON_STYLE_KEY, ButtonStyle.DEFAULT);
            side = builder
                .comment(INVENTORY_SIDE_DESCRIPTION)
                .defineEnum(INVENTORY_SIDE_KEY, INVENTORY_SIDE_DEFAULT);
            showHoverTooltip = builder
                .comment(SHOW_HOVER_TOOLTIP_DESCRIPTION)
                .define(SHOW_HOVER_TOOLTIP_KEY, SHOW_HOVER_TOOLTIP_DEFAULT);
            maxColumns = builder
                .comment(MAX_COLUMNS_DESCRIPTION)
                .defineInRange(MAX_COLUMNS_KEY, MAX_COLUMNS_DEFAULT, MAX_COLUMNS_MIN, MAX_COLUMNS_MAX);
            maxColumnSlots = builder
                .comment(MAX_COLUMN_SLOTS_DESCRIPTION)
                .defineInRange(MAX_COLUMN_SLOTS_KEY, MAX_COLUMN_SLOTS_DEFAULT, MAX_COLUMN_SLOTS_MIN, MAX_COLUMN_SLOTS_MAX);
            maxColumnRenderSlots = builder
                .comment(MAX_COLUMN_RENDER_SLOTS_DESCRIPTION)
                .defineInRange(MAX_COLUMN_RENDER_SLOTS_KEY, MAX_COLUMN_RENDER_SLOTS_DEFAULT, MAX_COLUMN_RENDER_SLOTS_MIN, MAX_COLUMN_RENDER_SLOTS_MAX);
            Client.spec = builder.build();
        }

        public static ModConfigSpec getSpec() {
            return Client.spec;
        }

        @Override
        public boolean compatibilityMode() {
            return compatibilityMode.get();
        }

        @Override
        public ButtonStyle buttonStyle() {
            return buttonStyle.get();
        }

        @Override
        public Side side() {
            return side.get();
        }

        @Override
        public boolean showHoverTooltip() {
            return showHoverTooltip.get();
        }

        @Override
        public int maxColumns() {
            return maxColumns.get();
        }

        @Override
        public int maxColumnSlots() {
            return maxColumnSlots.get();
        }

        @Override
        public int maxColumnRenderSlots() {
            return maxColumnRenderSlots.get();
        }
    }

    public static final class Server implements OhmegaConfig.Server.Service {
        private static ModConfigSpec spec;

        public ModConfigSpec.ConfigValue<List<? extends String>> slotTypes;
        public ModConfigSpec.ConfigValue<List<? extends String>> keyboundSlotTypes;
        public ModConfigSpec.EnumValue<KeepAccessoriesBehaviour> keepAccessoriesBehaviour;
        public ModConfigSpec.BooleanValue disableAccessoryTypes;

        public Server() {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            slotTypes = builder
                .comment(SLOT_TYPES_DESCRIPTION)
                .defineList(SLOT_TYPES_KEY, SLOT_TYPES_DEFAULT, ACCESSORY_TYPE_VALIDATOR);
            keyboundSlotTypes = builder
                .comment(KEYBOUND_SLOT_TYPES_DESCRIPTION)
                .defineListAllowEmpty(KEYBOUND_SLOT_TYPES_KEY, KEYBOUND_SLOT_TYPES_DEFAULT, ACCESSORY_TYPE_VALIDATOR);
            keepAccessoriesBehaviour = builder
                .comment(KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION)
                .defineEnum(KEEP_ACCESSORIES_BEHAVIOUR_KEY, KeepAccessoriesBehaviour.DEFAULT);
            disableAccessoryTypes = builder
                .comment(DISABLE_ACCESSORY_TYPES_DESCRIPTION)
                .define(DISABLE_ACCESSORY_TYPES_KEY, DISABLE_ACCESSORY_TYPES_DEFAULT);
            Server.spec = builder.build();
        }

        public static ModConfigSpec getSpec() {
            return Server.spec;
        }

        @Override
        public List<? extends String> slotTypes() {
            return slotTypes.get();
        }

        @Override
        public Set<? extends String> keyboundSlotTypes() {
            return new HashSet<>(keyboundSlotTypes.get());
        }

        @Override
        public KeepAccessoriesBehaviour keepAccessoriesBehaviour() {
            return keepAccessoriesBehaviour.get();
        }

        @Override
        public boolean disableAccessoryTypes() {
            return disableAccessoryTypes.get();
        }
    }
}