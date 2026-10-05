package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.advancement.trigger.AccessoryChangeTrigger;
import com.swacky.ohmega.api.util.LoaderService;

public final class OhmegaCriteriaTriggers {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static AccessoryChangeTrigger getAccessoryChange() {
        return IMPL.getAccessoryChange();
    }

    @LoaderService
    public interface Service {
        String ACCESSORY_CHANGE_KEY = "accessory_change";

        AccessoryChangeTrigger getAccessoryChange();
    }
}
