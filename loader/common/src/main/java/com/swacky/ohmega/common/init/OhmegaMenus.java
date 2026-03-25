package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NonNull;

public final class OhmegaMenus {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static MenuType<@NonNull AccessoryInventoryMenu> getAccessoryMenu() {
        return IMPL.getAccessoryMenu();
    }

    public interface Service {
        MenuType<@NonNull AccessoryInventoryMenu> getAccessoryMenu();
    }
}
