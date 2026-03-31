package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.menu.AccessoryInventoryMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NonNull;

public final class OhmegaMenusImpl implements OhmegaMenus.Service {
    private static final MenuType<@NonNull AccessoryInventoryMenu> ACCESSORY_INVENTORY = register("accessory_menu",
            new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> MenuType<@NonNull T> register(String id, MenuType<@NonNull T> object) {
        return Registry.register(BuiltInRegistries.MENU, Ohmega.id(id), object);
    }

    public static void init() {}

    @Override
    public MenuType<@NonNull AccessoryInventoryMenu> getAccessoryMenu() {
        return ACCESSORY_INVENTORY;
    }
}
