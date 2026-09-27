package com.swacky.ohmega.common.config;

import com.swacky.ohmega.api.common.config.KeepAccessoriesBehaviour;
import com.swacky.ohmega.api.common.config.OhmegaServerConfig;
import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class OhmegaServerConfigImpl extends OhmegaConfigImpl implements OhmegaServerConfig.Service {
    private static ModConfigSpec spec;
    private final OhmegaServerConfig.Data data;

    public OhmegaServerConfigImpl() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        LazySavedValue<List<? extends String>> defaultSlotTypes = wrap(builder
                .comment(DEFAULT_SLOT_TYPES_DESCRIPTION)
                .defineList(DEFAULT_SLOT_TYPES_KEY, DEFAULT_SLOT_TYPES_DEFAULT, () -> DEFAULT_SLOT_TYPES_NEW_VALUE_DEFAULT, ACCESSORY_TYPE_VALIDATOR));
        BooleanLazySavedValue shrinkDefaultSlotTypes = wrap(builder
                .comment(SHRINK_DEFAULT_SLOT_TYPES_DESCRIPTION)
                .define(SHRINK_DEFAULT_SLOT_TYPES_KEY, SHRINK_DEFAULT_SLOT_TYPES_DEFAULT));
        LazySavedValue<List<? extends String>> keyboundSlotTypes = wrap(builder
                .comment(KEYBOUND_SLOT_TYPES_DESCRIPTION)
                .defineListAllowEmpty(KEYBOUND_SLOT_TYPES_KEY, KEYBOUND_SLOT_TYPES_DEFAULT, () -> KEYBOUND_SLOT_TYPES_NEW_VALUE_DEFAULT, ACCESSORY_TYPE_VALIDATOR));
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
        this.data = new OhmegaServerConfig.Data(
                defaultSlotTypes,
                shrinkDefaultSlotTypes,
                keyboundSlotTypes,
                keepAccessoriesBehaviour,
                disableAccessoryTypes,
                allowHideAccessories,
                injectVanillaClear);
        spec = builder.build();
    }

    public static ModConfigSpec getSpec() {
        return spec;
    }

    @Override
    public OhmegaServerConfig.@NonNull Data getData() {
        return data;
    }

    @Override
    public boolean isLoaded() {
        return spec.isLoaded();
    }
}