package com.swacky.ohmega.common.config;

import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.IntLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("unused")
public class OhmegaConfigImpl {
    protected static BooleanLazySavedValue wrap(ForgeConfigSpec.BooleanValue nativeValue) {
        return new BooleanLazySavedValue(nativeValue::get, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }

    protected static IntLazySavedValue wrap(ForgeConfigSpec.IntValue nativeValue) {
        return new IntLazySavedValue(nativeValue::get, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }

    protected static <T> LazySavedValue<T> wrap(ForgeConfigSpec.ConfigValue<T> nativeValue) {
        return new LazySavedValue<>(nativeValue, (value, last) -> {
            nativeValue.set(value);

            if (last) {
                nativeValue.save();
            }
        });
    }
}