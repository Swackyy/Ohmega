package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public final class OhmegaMenusImpl implements OhmegaMenus.Service {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, OhmegaCommon.MODID);

    public static final RegistryObject<MenuType<@NonNull AccessoryInventoryMenu>> ACCESSORY_INVENTORY = register("accessory_menu",
            () -> new MenuType<>(AccessoryInventoryMenu::new));

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<@NonNull T>> register(String id, Supplier<MenuType<@NonNull T>> sup) {
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
