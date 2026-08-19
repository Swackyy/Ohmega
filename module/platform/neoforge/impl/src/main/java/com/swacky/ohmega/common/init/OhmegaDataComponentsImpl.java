package com.swacky.ohmega.common.init;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OhmegaDataComponentsImpl implements OhmegaDataComponents.Service {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Ohmega.MODID);

    private static final Supplier<DataComponentType<ItemAttributeModifiers>> ACCESSORY_ACTIVE_MODIFIERS = register(ACCESSORY_ACTIVE_MODIFIERS_KEY, OhmegaDataComponents::createItemAttributeModifiers);
    private static final Supplier<DataComponentType<Boolean>> ACTIVE = register(ACTIVE_KEY, OhmegaDataComponents::createActive);
    private static final Supplier<DataComponentType<Integer>> SLOT_INDEX = register(SLOT_INDEX_KEY, OhmegaDataComponents::createSlotIndex);
    private static final Supplier<DataComponentType<ItemAttributeModifiers>> SLOT_ACTIVE_MODIFIERS = register(SLOT_ACTIVE_MODIFIERS_KEY, OhmegaDataComponents::createItemAttributeModifiers);

    private static <T> Supplier<DataComponentType<T>> register(String id, Supplier<DataComponentType<T>> supplier) {
        return DATA_COMPONENTS.register(id, supplier);
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
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
