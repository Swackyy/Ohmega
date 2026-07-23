package com.swacky.ohmega.config;

import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class OhmegaConfigImpl {
    private static BooleanLazySavedValue wrap(ForgeConfigSpec.BooleanValue nativeValue) {
        return new BooleanLazySavedValue(nativeValue::get, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }

    private static IntLazySavedValue wrap(ForgeConfigSpec.IntValue nativeValue) {
        return new IntLazySavedValue(nativeValue::get, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }

    private static <T> LazySavedValue<T> wrap(ForgeConfigSpec.ConfigValue<T> nativeValue) {
        return new LazySavedValue<>(nativeValue, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }

    public static final class Client implements OhmegaConfig.Client.Service {
        private static ForgeConfigSpec spec;

        private final Data data;

        public Client() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            BooleanLazySavedValue compatibilityMode = wrap(builder
                    .comment(COMPATIBILITY_MODE_DESCRIPTION)
                    .define(COMPATIBILITY_MODE_KEY, COMPATIBILITY_MODE_DEFAULT));
            BooleanLazySavedValue showTranslationToast = wrap(builder
                    .comment(SHOW_TRANSLATION_TOAST_DESCRIPTION)
                    .define(SHOW_TRANSLATION_TOAST_KEY, SHOW_TRANSLATION_TOAST_DEFAULT));
            LazySavedValue<ButtonStyle> toggleExtensionButtonStyle = wrap(builder
                    .comment(TOGGLE_EXTENSION_BUTTON_STYLE_DESCRIPTION)
                    .defineEnum(TOGGLE_EXTENSION_BUTTON_STYLE_KEY, ButtonStyle.DEFAULT));
            LazySavedValue<String> accessoryExtensionId = wrap(builder
                    .comment(ACCESSORY_EXTENSION_ID_DESCRIPTION)
                    .define(ACCESSORY_EXTENSION_ID_KEY, ACCESSORY_EXTENSION_ID_DEFAULT, ACCESSORY_EXTENSION_ID_VALIDATOR));
            LazySavedValue<FillDirection> fillDirection = wrap(builder
                    .comment(FILL_DIRECTION_DESCRIPTION)
                    .defineEnum(FILL_DIRECTION_KEY, FILL_DIRECTION_DEFAULT));
            IntLazySavedValue maxColumns = wrap(builder
                    .comment(MAX_COLUMNS_DESCRIPTION)
                    .defineInRange(MAX_COLUMNS_KEY, MAX_COLUMNS_DEFAULT, MAX_COLUMNS_MIN, MAX_COLUMNS_MAX));
            IntLazySavedValue maxColumnSlots = wrap(builder
                    .comment(MAX_COLUMN_SLOTS_DESCRIPTION)
                    .defineInRange(MAX_COLUMN_SLOTS_KEY, MAX_COLUMN_SLOTS_DEFAULT, MAX_COLUMN_SLOTS_MIN, MAX_COLUMN_SLOTS_MAX));
            IntLazySavedValue maxColumnRenderSlots = wrap(builder
                    .comment(MAX_COLUMN_RENDER_SLOTS_DESCRIPTION)
                    .defineInRange(MAX_COLUMN_RENDER_SLOTS_KEY, MAX_COLUMN_RENDER_SLOTS_DEFAULT, MAX_COLUMN_RENDER_SLOTS_MIN, MAX_COLUMN_RENDER_SLOTS_MAX));
            BooleanLazySavedValue showHoverTooltip = wrap(builder
                    .comment(SHOW_HOVER_TOOLTIP_DESCRIPTION)
                    .define(SHOW_HOVER_TOOLTIP_KEY, SHOW_HOVER_TOOLTIP_DEFAULT));
            BooleanLazySavedValue renderAccessories = wrap(builder
                    .comment(RENDER_ACCESSORIES_DESCRIPTION)
                    .define(RENDER_ACCESSORIES_KEY, RENDER_ACCESSORIES_DEFAULT));

            builder.push(SECTION_EDIT_UI);

            IntLazySavedValue backgroundAlpha = wrap(builder
                    .comment(BACKGROUND_ALPHA_DESCRIPTION)
                    .defineInRange(BACKGROUND_ALPHA_KEY, BACKGROUND_ALPHA_DEFAULT, BACKGROUND_ALPHA_MIN, BACKGROUND_ALPHA_MAX));
            IntLazySavedValue magneticsStrength = wrap(builder
                    .comment(MAGNETICS_STRENGTH_DESCRIPTION)
                    .defineInRange(MAGNETICS_STRENGTH_KEY, MAGNETICS_STRENGTH_DEFAULT, MAGNETICS_STRENGTH_MIN, MAGNETICS_STRENGTH_MAX));

            builder.pop();
            builder.push(SECTION_POSITIONS);
            builder.push(SECTION_SURVIVAL);

            IntLazySavedValue survivalExtensionX = wrap(builder
                    .comment(SURVIVAL_EXTENSION_X_DESCRIPTION)
                    .defineInRange(SURVIVAL_EXTENSION_X_KEY, SURVIVAL_EXTENSION_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalExtensionY = wrap(builder
                    .comment(SURVIVAL_EXTENSION_Y_DESCRIPTION)
                    .defineInRange(SURVIVAL_EXTENSION_Y_KEY, SURVIVAL_EXTENSION_Y_DEFAULT, POSITION_MIN, POSITION_MAX));

            builder.push(SECTION_TOGGLE_EXTENSION_BUTTON);

            IntLazySavedValue survivalToggleExtensionButtonDefaultX = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalToggleExtensionButtonDefaultY = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalToggleExtensionButtonLegacyX = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalToggleExtensionButtonLegacyY = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalToggleExtensionButtonTagLeftX = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalToggleExtensionButtonTagLeftY = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalToggleExtensionButtonTagRightX = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalToggleExtensionButtonTagRightY = wrap(builder
                    .comment(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION)
                    .defineInRange(SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY, SURVIVAL_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DEFAULT, POSITION_MIN, POSITION_MAX));

            builder.pop();

            IntLazySavedValue survivalFlipEntityButtonX = wrap(builder
                    .comment(SURVIVAL_FLIP_ENTITY_BUTTON_X_DESCRIPTION)
                    .defineInRange(SURVIVAL_FLIP_ENTITY_BUTTON_X_KEY, SURVIVAL_FLIP_ENTITY_BUTTON_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue survivalFlipEntityButtonY = wrap(builder
                    .comment(SURVIVAL_FLIP_ENTITY_BUTTON_Y_DESCRIPTION)
                    .defineInRange(SURVIVAL_FLIP_ENTITY_BUTTON_Y_KEY, SURVIVAL_FLIP_ENTITY_BUTTON_Y_DEFAULT, POSITION_MIN, POSITION_MAX));

            builder.pop();
            builder.push(SECTION_CREATIVE);

            IntLazySavedValue creativeExtensionX = wrap(builder
                    .comment(CREATIVE_EXTENSION_X_DESCRIPTION)
                    .defineInRange(CREATIVE_EXTENSION_X_KEY, CREATIVE_EXTENSION_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeExtensionY = wrap(builder
                    .comment(CREATIVE_EXTENSION_Y_DESCRIPTION)
                    .defineInRange(CREATIVE_EXTENSION_Y_KEY, CREATIVE_EXTENSION_Y_DEFAULT, POSITION_MIN, POSITION_MAX));

            builder.push(SECTION_TOGGLE_EXTENSION_BUTTON);

            IntLazySavedValue creativeToggleExtensionButtonDefaultX = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeToggleExtensionButtonDefaultY = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_DEFAULT_Y_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeToggleExtensionButtonLegacyX = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeToggleExtensionButtonLegacyY = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_LEGACY_Y_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeToggleExtensionButtonTagLeftX = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeToggleExtensionButtonTagLeftY = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_LEFT_Y_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeToggleExtensionButtonTagRightX = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeToggleExtensionButtonTagRightY = wrap(builder
                    .comment(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DESCRIPTION)
                    .defineInRange(CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_KEY, CREATIVE_TOGGLE_EXTENSION_BUTTON_TAG_RIGHT_Y_DEFAULT, POSITION_MIN, POSITION_MAX));

            builder.pop();

            IntLazySavedValue creativeFlipEntityButtonX = wrap(builder
                    .comment(CREATIVE_FLIP_ENTITY_BUTTON_X_DESCRIPTION)
                    .defineInRange(CREATIVE_FLIP_ENTITY_BUTTON_X_KEY, CREATIVE_FLIP_ENTITY_BUTTON_X_DEFAULT, POSITION_MIN, POSITION_MAX));
            IntLazySavedValue creativeFlipEntityButtonY = wrap(builder
                    .comment(CREATIVE_FLIP_ENTITY_BUTTON_Y_DESCRIPTION)
                    .defineInRange(CREATIVE_FLIP_ENTITY_BUTTON_Y_KEY, CREATIVE_FLIP_ENTITY_BUTTON_Y_DEFAULT, POSITION_MIN, POSITION_MAX));
            this.data = new Data(
                    compatibilityMode,
                    showTranslationToast,
                    toggleExtensionButtonStyle,
                    accessoryExtensionId,
                    fillDirection,
                    maxColumns,
                    maxColumnSlots,
                    maxColumnRenderSlots,
                    showHoverTooltip,
                    renderAccessories,
                    backgroundAlpha,
                    magneticsStrength,
                    survivalExtensionX,
                    survivalExtensionY,
                    survivalToggleExtensionButtonDefaultX,
                    survivalToggleExtensionButtonDefaultY,
                    survivalToggleExtensionButtonLegacyX,
                    survivalToggleExtensionButtonLegacyY,
                    survivalToggleExtensionButtonTagLeftX,
                    survivalToggleExtensionButtonTagLeftY,
                    survivalToggleExtensionButtonTagRightX,
                    survivalToggleExtensionButtonTagRightY,
                    survivalFlipEntityButtonX,
                    survivalFlipEntityButtonY,
                    creativeExtensionX,
                    creativeExtensionY,
                    creativeToggleExtensionButtonDefaultX,
                    creativeToggleExtensionButtonDefaultY,
                    creativeToggleExtensionButtonLegacyX,
                    creativeToggleExtensionButtonLegacyY,
                    creativeToggleExtensionButtonTagLeftX,
                    creativeToggleExtensionButtonTagLeftY,
                    creativeToggleExtensionButtonTagRightX,
                    creativeToggleExtensionButtonTagRightY,
                    creativeFlipEntityButtonX,
                    creativeFlipEntityButtonY);
            spec = builder.build();
        }

        public static ForgeConfigSpec getSpec() {
            return Client.spec;
        }

        @Override
        public Data getData() {
            return data;
        }

        @Override
        public boolean isLoaded() {
            return spec.isLoaded();
        }
    }

    public static final class Server implements OhmegaConfig.Server.Service {
        private static ForgeConfigSpec spec;

        private final Data data;

        public Server() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            LazySavedValue<List<? extends String>> defaultSlotTypes = wrap(builder
                    .comment(DEFAULT_SLOT_TYPES_DESCRIPTION)
                    .defineList(DEFAULT_SLOT_TYPES_KEY, DEFAULT_SLOT_TYPES_DEFAULT, ACCESSORY_TYPE_VALIDATOR));
            BooleanLazySavedValue shrinkDefaultSlotTypes = wrap(builder
                    .comment(SHRINK_DEFAULT_SLOT_TYPES_DESCRIPTION)
                    .define(SHRINK_DEFAULT_SLOT_TYPES_KEY, SHRINK_DEFAULT_SLOT_TYPES_DEFAULT));
            LazySavedValue<List<? extends String>> keyboundSlotTypes = wrap(builder
                    .comment(KEYBOUND_SLOT_TYPES_DESCRIPTION)
                    .defineListAllowEmpty(KEYBOUND_SLOT_TYPES_KEY, KEYBOUND_SLOT_TYPES_DEFAULT, ACCESSORY_TYPE_VALIDATOR));
            LazySavedValue<KeepAccessoriesBehaviour> keepAccessoriesBehaviour = wrap(builder
                    .comment(KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION)
                    .defineEnum(KEEP_ACCESSORIES_BEHAVIOUR_KEY, KeepAccessoriesBehaviour.DEFAULT));
            BooleanLazySavedValue disableAccessoryTypes = wrap(builder
                    .comment(DISABLE_ACCESSORY_TYPES_DESCRIPTION)
                    .define(DISABLE_ACCESSORY_TYPES_KEY, DISABLE_ACCESSORY_TYPES_DEFAULT));
            BooleanLazySavedValue allowHideAccessories = wrap(builder
                    .comment(ALLOW_HIDE_ACCESSORIES_DESCRIPTION)
                    .define(ALLOW_HIDE_ACCESSORIES_KEY, ALLOW_HIDE_ACCESSORIES_DEFAULT));
            BooleanLazySavedValue injectVanillaClear = wrap(builder
                    .comment(INJECT_VANILLA_CLEAR_DESCRIPTION)
                    .define(INJECT_VANILLA_CLEAR_KEY, INJECT_VANILLA_CLEAR_DEFAULT));
            this.data = new Data(
                    defaultSlotTypes,
                    shrinkDefaultSlotTypes,
                    keyboundSlotTypes,
                    keepAccessoriesBehaviour,
                    disableAccessoryTypes,
                    allowHideAccessories,
                    injectVanillaClear);
            Server.spec = builder.build();
        }

        public static ForgeConfigSpec getSpec() {
            return Server.spec;
        }

        @Override
        public Data getData() {
            return data;
        }

        @Override
        public boolean isLoaded() {
            return spec.isLoaded();
        }
    }
}