package com.swacky.ohmega.common.core.init;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.datacomponent.AccessoryDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class OhmegaDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Ohmega.MODID);

    public static final RegistryObject<DataComponentType<AccessoryDataComponent>> ACCESSORY = DATA_COMPONENTS.register(
            "accessory", () -> DataComponentType.<AccessoryDataComponent>builder()
                    .persistent(AccessoryDataComponent.CODEC)
                    .networkSynchronized(AccessoryDataComponent.STREAM_CODEC)
                    .cacheEncoding()
                    .build());
}
