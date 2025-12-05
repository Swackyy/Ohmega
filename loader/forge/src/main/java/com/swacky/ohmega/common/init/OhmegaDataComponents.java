package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.datacomponent.AccessoryItemDataComponent;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class OhmegaDataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OhmegaCommon.MODID);

    public static final RegistryObject<DataComponentType<AccessoryItemDataComponent>> ACCESSORY_ITEM = register("accessory",
            () -> DataComponentType.<AccessoryItemDataComponent>builder()
                    .persistent(AccessoryItemDataComponent.CODEC)
                    .networkSynchronized(AccessoryItemDataComponent.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    private static <T> RegistryObject<DataComponentType<T>> register(String id, Supplier<DataComponentType<T>> sup) {
        return DATA_COMPONENTS.register(id, sup);
    }

    public static void register(BusGroup group) {
        DATA_COMPONENTS.register(group);
    }
}
