package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OhmegaMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, OhmegaCommon.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AccessoryInventoryMenu>> ACCESSORY_INVENTORY = MENUS.register("accessory_container",
            () -> new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));
}
