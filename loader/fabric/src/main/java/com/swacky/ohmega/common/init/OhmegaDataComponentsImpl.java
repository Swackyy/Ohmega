package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class OhmegaDataComponentsImpl implements  OhmegaDataComponents.Service {
    public static final DataComponentType<Boolean> ACTIVE = register(
            ACTIVE_KEY, OhmegaDataComponents.createActive());

    public static final DataComponentType<Integer> SLOT_INDEX = register(
            SLOT_INDEX_KEY, OhmegaDataComponents.createSlotIndex());

    private static <T> DataComponentType<T> register(String id, DataComponentType<T> object) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Ohmega.id(id), object);
    }

    public static void init() {}

    @Override
    public DataComponentType<Boolean> getActive() {
        return ACTIVE;
    }

    @Override
    public DataComponentType<Integer> getSlotIndex() {
        return SLOT_INDEX;
    }
}
