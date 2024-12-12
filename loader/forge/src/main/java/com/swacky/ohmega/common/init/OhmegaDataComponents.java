package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.datacomponent.AccessoryItemDataComponent;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class OhmegaDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OhmegaCommon.MODID);

    public static final RegistryObject<DataComponentType<AccessoryItemDataComponent>> ACCESSORY_ITEM = DATA_COMPONENTS.register(
            "accessory", () -> DataComponentType.<AccessoryItemDataComponent>builder()
                    .persistent(AccessoryItemDataComponent.CODEC)
                    .networkSynchronized(AccessoryItemDataComponent.STREAM_CODEC)
                    .cacheEncoding()
                    .build());
}
