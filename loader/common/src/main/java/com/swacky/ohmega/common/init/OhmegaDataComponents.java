package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.datacomponent.AccessoryItemDataComponent;
import net.minecraft.core.component.DataComponentType;

public final class OhmegaDataComponents {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static DataComponentType<AccessoryItemDataComponent> getItemDataComponent() {
        return IMPL.getItemDataComponent();
    }

    public interface Service {
        DataComponentType<AccessoryItemDataComponent> getItemDataComponent();
    }
}
