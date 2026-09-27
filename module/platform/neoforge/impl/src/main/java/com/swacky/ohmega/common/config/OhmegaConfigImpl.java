package com.swacky.ohmega.common.config;

import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import net.neoforged.neoforge.common.ModConfigSpec;

public class OhmegaConfigImpl {
    protected static BooleanLazySavedValue wrap(ModConfigSpec.BooleanValue nativeValue) {
        return new BooleanLazySavedValue(nativeValue::get, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }

    protected static IntLazySavedValue wrap(ModConfigSpec.IntValue nativeValue) {
        return new IntLazySavedValue(nativeValue::get, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }

    protected static <T> LazySavedValue<T> wrap(ModConfigSpec.ConfigValue<T> nativeValue) {
        return new LazySavedValue<>(nativeValue, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }
}
