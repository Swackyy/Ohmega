package com.swacky.ohmega.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OhmegaConfigImpl {
    public static final class Client implements OhmegaConfig.Client.Service {
        private static ModConfigSpec spec;

        private final ModConfigSpec.BooleanValue compatibilityMode;
        private final ModConfigSpec.EnumValue<ButtonStyle> buttonStyle;
        private final ModConfigSpec.EnumValue<Side> side;
        private final ModConfigSpec.BooleanValue showHoverTooltip;
        private final ModConfigSpec.IntValue maxColumns;
        private final ModConfigSpec.IntValue maxColumnSlots;
        private final ModConfigSpec.IntValue maxColumnRenderSlots;
        private final ModConfigSpec.BooleanValue showTranslationToast;

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
            showTranslationToast = builder
                    .comment(SHOW_TRANSLATION_TOAST_DESCRIPTION)
                    .define(SHOW_TRANSLATION_TOAST_KEY, SHOW_TRANSLATION_TOAST_DEFAULT);
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

        @Override
        public boolean showTranslationToast() {
            return showTranslationToast.get();
        }

        @Override
        public void setShowTranslationToast(boolean value) {
            showTranslationToast.set(value);
            spec.save();
        }

        @Override
        public boolean isLoaded() {
            return spec.isLoaded();
        }
    }

    public static final class Server implements OhmegaConfig.Server.Service {
        private static ModConfigSpec spec;

        private final ModConfigSpec.ConfigValue<List<? extends String>> slotTypes;
        private final ModConfigSpec.ConfigValue<List<? extends String>> keyboundSlotTypes;
        private final ModConfigSpec.EnumValue<KeepAccessoriesBehaviour> keepAccessoriesBehaviour;
        private final ModConfigSpec.BooleanValue disableAccessoryTypes;
        private final ModConfigSpec.BooleanValue allowHideAccessories;

        public Server() {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            slotTypes = builder
                    .comment(SLOT_TYPES_DESCRIPTION)
                    .defineList(SLOT_TYPES_KEY, SLOT_TYPES_DEFAULT, () -> SLOT_TYPES_NEW_VALUE_DEFAULT, ACCESSORY_TYPE_VALIDATOR);
            keyboundSlotTypes = builder
                    .comment(KEYBOUND_SLOT_TYPES_DESCRIPTION)
                    .defineListAllowEmpty(KEYBOUND_SLOT_TYPES_KEY, KEYBOUND_SLOT_TYPES_DEFAULT, () -> KEYBOUND_SLOT_TYPES_NEW_VALUE_DEFAULT, ACCESSORY_TYPE_VALIDATOR);
            keepAccessoriesBehaviour = builder
                    .comment(KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION)
                    .defineEnum(KEEP_ACCESSORIES_BEHAVIOUR_KEY, KeepAccessoriesBehaviour.DEFAULT);
            disableAccessoryTypes = builder
                    .comment(DISABLE_ACCESSORY_TYPES_DESCRIPTION)
                    .define(DISABLE_ACCESSORY_TYPES_KEY, DISABLE_ACCESSORY_TYPES_DEFAULT);
            allowHideAccessories = builder
                    .comment(ALLOW_HIDE_ACCESSORIES_DESCRIPTION)
                    .define(ALLOW_HIDE_ACCESSORIES_KEY, ALLOW_HIDE_ACCESSORIES_DEFAULT);
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

        @Override
        public boolean allowHideAccessories() {
            return allowHideAccessories.get();
        }

        @Override
        public boolean isLoaded() {
            return spec.isLoaded();
        }
    }
}