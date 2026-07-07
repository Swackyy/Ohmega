package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.advancement.trigger.AccessoryChangeTrigger;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OhmegaCriteriaTriggersImpl implements OhmegaCriteriaTriggers.Service {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, Ohmega.MODID);

    private static final DeferredHolder<CriterionTrigger<?>, AccessoryChangeTrigger> ACCESSORY_CHANGE = register(ACCESSORY_CHANGE_KEY, AccessoryChangeTrigger::new);

    private static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String id, Supplier<T> supplier) {
        return TRIGGERS.register(id, supplier);
    }

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }

    @Override
    public AccessoryChangeTrigger getAccessoryChange() {
        return ACCESSORY_CHANGE.get();
    }
}
