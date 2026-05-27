package com.swacky.ohmega.config;

import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class OhmegaConfigImpl {
    private static BooleanLazySavedValue wrapBoolean(ModConfigSpec.ConfigValue<Boolean> nativeValue) {
        return new BooleanLazySavedValue(nativeValue::get, nativeValue::set);
    }

    private static IntLazySavedValue wrapInt(ModConfigSpec.ConfigValue<Integer> nativeValue) {
        return new IntLazySavedValue(nativeValue::get, nativeValue::set);
    }

    private static <T> LazySavedValue<T> wrapObject(ModConfigSpec.ConfigValue<T> nativeValue) {
        return new LazySavedValue<>(nativeValue, nativeValue::set);
    }

    public static final class Client implements OhmegaConfig.Client.Service {
        private static ModConfigSpec spec;

        private final Data data;

        public Client() {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            data = new Data(
                    wrapBoolean(builder
                            .comment(COMPATIBILITY_MODE_DESCRIPTION)
                            .define(COMPATIBILITY_MODE_KEY, COMPATIBILITY_MODE_DEFAULT)),
                    wrapObject(builder
                            .comment(BUTTON_STYLE_DESCRIPTION)
                            .defineEnum(BUTTON_STYLE_KEY, ButtonStyle.DEFAULT)),
                    wrapObject(builder
                            .comment(FILL_DIRECTION_DESCRIPTION)
                            .defineEnum(FILL_DIRECTION_KEY, FILL_DIRECTION_DEFAULT)),
                    wrapBoolean(builder
                            .comment(SHOW_HOVER_TOOLTIP_DESCRIPTION)
                            .define(SHOW_HOVER_TOOLTIP_KEY, SHOW_HOVER_TOOLTIP_DEFAULT)),
                    wrapInt(builder
                            .comment(MAX_COLUMNS_DESCRIPTION)
                            .defineInRange(MAX_COLUMNS_KEY, MAX_COLUMNS_DEFAULT, MAX_COLUMNS_MIN, MAX_COLUMNS_MAX)),
                    wrapInt(builder
                            .comment(MAX_COLUMN_SLOTS_DESCRIPTION)
                            .defineInRange(MAX_COLUMN_SLOTS_KEY, MAX_COLUMN_SLOTS_DEFAULT, MAX_COLUMN_SLOTS_MIN, MAX_COLUMN_SLOTS_MAX)),
                    wrapInt(builder
                            .comment(MAX_COLUMN_RENDER_SLOTS_DESCRIPTION)
                            .defineInRange(MAX_COLUMN_RENDER_SLOTS_KEY, MAX_COLUMN_RENDER_SLOTS_DEFAULT, MAX_COLUMN_RENDER_SLOTS_MIN, MAX_COLUMN_RENDER_SLOTS_MAX)),
                    wrapBoolean(builder
                            .comment(SHOW_TRANSLATION_TOAST_DESCRIPTION)
                            .define(SHOW_TRANSLATION_TOAST_KEY, SHOW_TRANSLATION_TOAST_DEFAULT)),
                    wrapInt(builder
                            .comment(SURVIVAL_EXTENSION_X_DESCRIPTION)
                            .defineInRange(SURVIVAL_EXTENSION_X_KEY, SURVIVAL_EXTENSION_X_DEFAULT, SURVIVAL_EXTENSION_X_MIN, SURVIVAL_EXTENSION_X_MAX)),
                    wrapInt(builder
                            .comment(SURVIVAL_EXTENSION_Y_DESCRIPTION)
                            .defineInRange(SURVIVAL_EXTENSION_Y_KEY, SURVIVAL_EXTENSION_Y_DEFAULT, SURVIVAL_EXTENSION_Y_MIN, SURVIVAL_EXTENSION_Y_MAX)),
                    wrapInt(builder
                            .comment(CREATIVE_EXTENSION_X_DESCRIPTION)
                            .defineInRange(CREATIVE_EXTENSION_X_KEY, CREATIVE_EXTENSION_X_DEFAULT, CREATIVE_EXTENSION_X_MIN, CREATIVE_EXTENSION_X_MAX)),
                    wrapInt(builder
                            .comment(CREATIVE_EXTENSION_Y_DESCRIPTION)
                            .defineInRange(CREATIVE_EXTENSION_Y_KEY, CREATIVE_EXTENSION_Y_DEFAULT, CREATIVE_EXTENSION_Y_MIN, CREATIVE_EXTENSION_Y_MAX)),
                    wrapObject(builder
                            .comment(ACCESSORY_EXTENSION_ID_DESCRIPTION)
                            .define(ACCESSORY_EXTENSION_ID_KEY, ACCESSORY_EXTENSION_ID_DEFAULT)));
            Client.spec = builder.build();
        }

        public static ModConfigSpec getSpec() {
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
        private static ModConfigSpec spec;

        private final Data data;

        public Server() {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            data = new Data(
                    wrapObject(builder
                            .comment(SLOT_TYPES_DESCRIPTION)
                            .defineList(SLOT_TYPES_KEY, SLOT_TYPES_DEFAULT, () -> SLOT_TYPES_NEW_VALUE_DEFAULT, ACCESSORY_TYPE_VALIDATOR)),
                    wrapObject(builder
                            .comment(KEYBOUND_SLOT_TYPES_DESCRIPTION)
                            .defineListAllowEmpty(KEYBOUND_SLOT_TYPES_KEY, KEYBOUND_SLOT_TYPES_DEFAULT, () -> KEYBOUND_SLOT_TYPES_NEW_VALUE_DEFAULT, ACCESSORY_TYPE_VALIDATOR)),
                    wrapObject(builder
                            .comment(KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION)
                            .defineEnum(KEEP_ACCESSORIES_BEHAVIOUR_KEY, KeepAccessoriesBehaviour.DEFAULT)),
                    wrapBoolean(builder
                            .comment(DISABLE_ACCESSORY_TYPES_DESCRIPTION)
                            .define(DISABLE_ACCESSORY_TYPES_KEY, DISABLE_ACCESSORY_TYPES_DEFAULT)),
                    wrapBoolean(builder
                            .comment(ALLOW_HIDE_ACCESSORIES_DESCRIPTION)
                            .define(ALLOW_HIDE_ACCESSORIES_KEY, ALLOW_HIDE_ACCESSORIES_DEFAULT)));
            Server.spec = builder.build();
        }

        public static ModConfigSpec getSpec() {
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
