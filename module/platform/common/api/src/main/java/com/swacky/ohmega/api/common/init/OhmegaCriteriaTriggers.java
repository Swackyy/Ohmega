package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.advancement.trigger.AccessoryChangeTrigger;

public final class OhmegaCriteriaTriggers {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static AccessoryChangeTrigger getAccessoryChange() {
        return IMPL.getAccessoryChange();
    }

    public interface Service {
        String ACCESSORY_CHANGE_KEY = "accessory_change";

        AccessoryChangeTrigger getAccessoryChange();
    }
}
