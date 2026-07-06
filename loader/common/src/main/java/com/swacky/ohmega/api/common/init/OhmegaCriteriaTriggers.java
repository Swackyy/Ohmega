package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.advancement.trigger.AccessoryChangeTrigger;
import com.swacky.ohmega.common.Ohmega;

public final class OhmegaCriteriaTriggers {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static AccessoryChangeTrigger getAccessoryChange() {
        return IMPL.getAccessoryChange();
    }

    public interface Service {
        AccessoryChangeTrigger getAccessoryChange();
    }
}
