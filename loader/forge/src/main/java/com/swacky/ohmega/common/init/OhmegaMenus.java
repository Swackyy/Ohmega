package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OhmegaMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, OhmegaCommon.MODID);

    public static final RegistryObject<MenuType<@NotNull AccessoryInventoryMenu>> ACCESSORY_INVENTORY = register("accessory_container",
            () -> new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<@NotNull T>> register(String id, Supplier<MenuType<@NotNull T>> sup) {
        return MENUS.register(id, sup);
    }

    public static void register(BusGroup group) {
        MENUS.register(group);
    }
}
