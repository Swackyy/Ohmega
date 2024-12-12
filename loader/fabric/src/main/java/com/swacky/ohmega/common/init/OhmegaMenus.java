package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class OhmegaMenus {
    public static final MenuType<AccessoryInventoryMenu> ACCESSORY_INVENTORY = register("accessory_container",
            new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType<T> object) {
        return Registry.register(BuiltInRegistries.MENU, OhmegaCommon.rl(name), object);
    }

    public static void init() {}
}
