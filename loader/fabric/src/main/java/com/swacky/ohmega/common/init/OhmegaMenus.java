package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class OhmegaMenus {
    public static final MenuType<@NotNull AccessoryInventoryMenu> ACCESSORY_INVENTORY = register("accessory_container",
            new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> MenuType<@NotNull T> register(String id, MenuType<@NotNull T> object) {
        return Registry.register(BuiltInRegistries.MENU, OhmegaCommon.id(id), object);
    }

    public static void init() {}
}
