package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OhmegaMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, OhmegaCommon.MODID);

    public static final Supplier<MenuType<@NotNull AccessoryInventoryMenu>> ACCESSORY_INVENTORY = register("accessory_container",
            () -> new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> Supplier<MenuType<@NotNull T>> register(String id, Supplier<MenuType<@NotNull T>> sup) {
        return MENUS.register(id, sup);
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
