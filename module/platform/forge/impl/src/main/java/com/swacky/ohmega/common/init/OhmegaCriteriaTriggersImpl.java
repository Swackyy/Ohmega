package com.swacky.ohmega.common.init;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.advancement.trigger.AccessoryChangeTrigger;
import com.swacky.ohmega.api.common.init.OhmegaCriteriaTriggers;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class OhmegaCriteriaTriggersImpl implements OhmegaCriteriaTriggers.Service {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, Ohmega.MODID);

    private static final RegistryObject<AccessoryChangeTrigger> ACCESSORY_CHANGE = register(ACCESSORY_CHANGE_KEY, AccessoryChangeTrigger::new);

    private static <T extends CriterionTrigger<?>> RegistryObject<T> register(String id, Supplier<T> supplier) {
        return TRIGGERS.register(id, supplier);
    }

    public static void register(BusGroup group) {
        TRIGGERS.register(group);
    }

    @Override
    public AccessoryChangeTrigger getAccessoryChange() {
        return ACCESSORY_CHANGE.get();
    }
}
