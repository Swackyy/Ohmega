package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.advancement.trigger.AccessoryChangeTrigger;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class OhmegaCriteriaTriggersImpl implements OhmegaCriteriaTriggers.Service {
    private static final AccessoryChangeTrigger ACCESSORY_CHANGE = register(ACCESSORY_CHANGE_KEY, new AccessoryChangeTrigger());

    private static <T extends CriterionTrigger<?>> T register(String id, T trigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Ohmega.id(id), trigger);
    }

    @Override
    public AccessoryChangeTrigger getAccessoryChange() {
        return ACCESSORY_CHANGE;
    }
}
