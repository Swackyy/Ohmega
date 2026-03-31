package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.menu.AccessoryInventoryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public final class OhmegaMenusImpl implements OhmegaMenus.Service {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Ohmega.MODID);

    private static final Supplier<MenuType<@NonNull AccessoryInventoryMenu>> ACCESSORY_INVENTORY = register("accessory_menu",
            () -> new MenuType<>(AccessoryInventoryMenu::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> Supplier<MenuType<@NonNull T>> register(String id, Supplier<MenuType<@NonNull T>> sup) {
        return MENUS.register(id, sup);
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }

    @Override
    public MenuType<@NonNull AccessoryInventoryMenu> getAccessoryMenu() {
        return ACCESSORY_INVENTORY.get();
    }
}
