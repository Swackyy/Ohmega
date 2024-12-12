package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.datacomponent.AccessoryItemDataComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class OhmegaDataComponents {
    public static final DataComponentType<AccessoryItemDataComponent> ACCESSORY_ITEM = register(
            "accessory", DataComponentType.<AccessoryItemDataComponent>builder()
                    .persistent(AccessoryItemDataComponent.CODEC)
                    .networkSynchronized(AccessoryItemDataComponent.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> object) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, OhmegaCommon.rl(name), object);
    }

    public static void init() {}
}
