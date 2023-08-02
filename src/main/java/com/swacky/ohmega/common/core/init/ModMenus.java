package com.swacky.ohmega.common.core.init;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Ohmega.MODID);

    public static final RegistryObject<MenuType<AccessoryInventoryMenu>> ACCESSORY_INVENTORY = MENUS.register("accessory_container",
            () -> new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));
}
