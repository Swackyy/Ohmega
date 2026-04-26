package com.swacky.ohmega.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OhmegaConfigImpl {
    public static final class Client implements OhmegaConfig.Client.Service {
        private static ForgeConfigSpec spec;

        private final ForgeConfigSpec.BooleanValue compatibilityMode;
        private final ForgeConfigSpec.EnumValue<ButtonStyle> buttonStyle;
        private final ForgeConfigSpec.EnumValue<FillDirection> fillDirection;
        private final ForgeConfigSpec.BooleanValue showHoverTooltip;
        private final ForgeConfigSpec.IntValue maxColumns;
        private final ForgeConfigSpec.IntValue maxColumnSlots;
        private final ForgeConfigSpec.IntValue maxColumnRenderSlots;
        private final ForgeConfigSpec.BooleanValue showTranslationToast;
        private final ForgeConfigSpec.IntValue survivalExtensionX;
        private final ForgeConfigSpec.IntValue survivalExtensionY;
        private final ForgeConfigSpec.IntValue creativeExtensionX;
        private final ForgeConfigSpec.IntValue creativeExtensionY;

        public Client() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            compatibilityMode = builder
                    .comment(COMPATIBILITY_MODE_DESCRIPTION)
                    .define(COMPATIBILITY_MODE_KEY, COMPATIBILITY_MODE_DEFAULT);
            buttonStyle = builder
                    .comment(BUTTON_STYLE_DESCRIPTION)
                    .defineEnum(BUTTON_STYLE_KEY, ButtonStyle.DEFAULT);
            fillDirection = builder
                    .comment(FILL_DIRECTION_DESCRIPTION)
                    .defineEnum(FILL_DIRECTION_KEY, FILL_DIRECTION_DEFAULT);
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
            survivalExtensionX = builder
                    .comment(SURVIVAL_EXTENSION_X_DESCRIPTION)
                    .defineInRange(SURVIVAL_EXTENSION_X_KEY, SURVIVAL_EXTENSION_X_DEFAULT, SURVIVAL_EXTENSION_X_MIN, SURVIVAL_EXTENSION_X_MAX);
            survivalExtensionY = builder
                    .comment(SURVIVAL_EXTENSION_Y_DESCRIPTION)
                    .defineInRange(SURVIVAL_EXTENSION_Y_KEY, SURVIVAL_EXTENSION_Y_DEFAULT, SURVIVAL_EXTENSION_Y_MIN, SURVIVAL_EXTENSION_Y_MAX);
            creativeExtensionX = builder
                    .comment(CREATIVE_EXTENSION_X_DESCRIPTION)
                    .defineInRange(CREATIVE_EXTENSION_X_KEY, CREATIVE_EXTENSION_X_DEFAULT, CREATIVE_EXTENSION_X_MIN, CREATIVE_EXTENSION_X_MAX);
            creativeExtensionY = builder
                    .comment(CREATIVE_EXTENSION_Y_DESCRIPTION)
                    .defineInRange(CREATIVE_EXTENSION_Y_KEY, CREATIVE_EXTENSION_Y_DEFAULT, CREATIVE_EXTENSION_Y_MIN, CREATIVE_EXTENSION_Y_MAX);
            Client.spec = builder.build();
        }

        public static ForgeConfigSpec getSpec() {
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
        public FillDirection fillDirection() {
            return fillDirection.get();
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
        public int survivalExtensionX() {
            return survivalExtensionX.get();
        }

        @Override
        public int survivalExtensionY() {
            return survivalExtensionY.get();
        }

        @Override
        public int creativeExtensionX() {
            return creativeExtensionX.get();
        }

        @Override
        public int creativeExtensionY() {
            return creativeExtensionY.get();
        }

        @Override
        public boolean isLoaded() {
            return spec.isLoaded();
        }
    }

    public static final class Server implements OhmegaConfig.Server.Service {
        private static ForgeConfigSpec spec;

        private final ForgeConfigSpec.ConfigValue<List<? extends String>> slotTypes;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> keyboundSlotTypes;
        private final ForgeConfigSpec.EnumValue<KeepAccessoriesBehaviour> keepAccessoriesBehaviour;
        private final ForgeConfigSpec.BooleanValue disableAccessoryTypes;
        private final ForgeConfigSpec.BooleanValue allowHideAccessories;
        private final ForgeConfigSpec.ConfigValue<String> menuExtensionId;

        public Server() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            slotTypes = builder
                    .comment(SLOT_TYPES_DESCRIPTION)
                    .defineListAllowEmpty(SLOT_TYPES_KEY, SLOT_TYPES_DEFAULT, ACCESSORY_TYPE_VALIDATOR);
            keyboundSlotTypes = builder
                    .comment(KEYBOUND_SLOT_TYPES_DESCRIPTION)
                    .defineListAllowEmpty(KEYBOUND_SLOT_TYPES_KEY, KEYBOUND_SLOT_TYPES_DEFAULT, ACCESSORY_TYPE_VALIDATOR);
            keepAccessoriesBehaviour = builder
                    .comment(KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION)
                    .defineEnum(KEEP_ACCESSORIES_BEHAVIOUR_KEY, KeepAccessoriesBehaviour.DEFAULT);
            disableAccessoryTypes = builder
                    .comment(DISABLE_ACCESSORY_TYPES_DESCRIPTION)
                    .define(DISABLE_ACCESSORY_TYPES_KEY, DISABLE_ACCESSORY_TYPES_DEFAULT);
            allowHideAccessories = builder
                    .comment(ALLOW_HIDE_ACCESSORIES_DESCRIPTION)
                    .define(ALLOW_HIDE_ACCESSORIES_KEY, ALLOW_HIDE_ACCESSORIES_DEFAULT);
            menuExtensionId = builder
                    .comment(MENU_EXTENSION_ID_DESCRIPTION)
                    .define(MENU_EXTENSION_ID_KEY, MENU_EXTENSION_ID_DEFAULT);
            Server.spec = builder.build();
        }

        public static ForgeConfigSpec getSpec() {
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
        public String menuExtensionId() {
            return menuExtensionId.get();
        }

        @Override
        public boolean isLoaded() {
            return spec.isLoaded();
        }
    }
}