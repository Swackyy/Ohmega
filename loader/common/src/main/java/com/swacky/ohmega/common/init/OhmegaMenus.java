package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NonNull;

public final class OhmegaMenus {
    private static final Service IMPL = OhmegaCommon.loadService(Service.class);

    public static MenuType<@NonNull AccessoryInventoryMenu> getAccessoryMenu() {
        return IMPL.getAccessoryMenu();
    }

    public interface Service {
        MenuType<@NonNull AccessoryInventoryMenu> getAccessoryMenu();
    }
}
