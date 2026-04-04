package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class OhmegaDataComponentsImpl implements OhmegaDataComponents.Service {
    private static final DataComponentType<Boolean> ACTIVE = register(ACTIVE_KEY, OhmegaDataComponents.createActive());
    private static final DataComponentType<Integer> SLOT_INDEX = register(SLOT_INDEX_KEY, OhmegaDataComponents.createSlotIndex());
    private static final DataComponentType<ItemAttributeModifiers> SLOT_ACTIVE_MODIFIERS = register(SLOT_ACTIVE_MODIFIERS_KEY, OhmegaDataComponents.createSlotActiveModifiers());

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

    @Override
    public DataComponentType<ItemAttributeModifiers> getSlotActiveModifiers() {
        return SLOT_ACTIVE_MODIFIERS;
    }
}
