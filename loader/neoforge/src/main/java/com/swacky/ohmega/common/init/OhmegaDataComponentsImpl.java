package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OhmegaDataComponentsImpl implements OhmegaDataComponents.Service {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Ohmega.MODID);

    private static final Supplier<DataComponentType<Boolean>> ACTIVE = register(ACTIVE_KEY, OhmegaDataComponents::createActive);
    private static final Supplier<DataComponentType<Integer>> SLOT_INDEX = register(SLOT_INDEX_KEY, OhmegaDataComponents::createSlotIndex);
    private static final Supplier<DataComponentType<ItemAttributeModifiers>> SLOT_ACTIVE_MODIFIERS = register(SLOT_ACTIVE_MODIFIERS_KEY, OhmegaDataComponents::createSlotActiveModifiers);

    private static <T> Supplier<DataComponentType<T>> register(String id, Supplier<DataComponentType<T>> sup) {
        return DATA_COMPONENTS.register(id, sup);
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
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
