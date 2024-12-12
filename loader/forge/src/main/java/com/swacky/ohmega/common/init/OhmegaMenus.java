package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OhmegaMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, OhmegaCommon.MODID);

    public static final RegistryObject<MenuType<AccessoryInventoryMenu>> ACCESSORY_INVENTORY = MENUS.register("accessory_container",
            () -> new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));
}
