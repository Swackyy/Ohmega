package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class OhmegaDataComponentsImpl implements OhmegaDataComponents.Service {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Ohmega.MODID);

    private static final RegistryObject<DataComponentType<ItemAttributeModifiers>> ACCESSORY_ACTIVE_MODIFIERS = register(ACCESSORY_ACTIVE_MODIFIERS_KEY, OhmegaDataComponents::createItemAttributeModifiers);
    private static final RegistryObject<DataComponentType<Boolean>> ACTIVE = register(ACTIVE_KEY, OhmegaDataComponents::createActive);
    private static final RegistryObject<DataComponentType<Integer>> SLOT_INDEX = register(SLOT_INDEX_KEY, OhmegaDataComponents::createSlotIndex);
    private static final RegistryObject<DataComponentType<ItemAttributeModifiers>> SLOT_ACTIVE_MODIFIERS = register(SLOT_ACTIVE_MODIFIERS_KEY, OhmegaDataComponents::createItemAttributeModifiers);

    private static <T> RegistryObject<DataComponentType<T>> register(String id, Supplier<DataComponentType<T>> supplier) {
        return DATA_COMPONENTS.register(id, supplier);
    }

    public static void register(BusGroup group) {
        DATA_COMPONENTS.register(group);
    }

    @Override
    public DataComponentType<ItemAttributeModifiers> getAccessoryActiveModifiers() {
        return ACCESSORY_ACTIVE_MODIFIERS.get();
    }

    @Override
    public DataComponentType<Boolean> getActive() {
        return ACTIVE.get();
    }

    @Override
    public DataComponentType<Integer> getSlotIndex() {
        return SLOT_INDEX.get();
    }

    @Override
    public DataComponentType<ItemAttributeModifiers> getSlotActiveModifiers() {
        return SLOT_ACTIVE_MODIFIERS.get();
    }
}
