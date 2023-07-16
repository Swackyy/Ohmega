package com.swacky.ohmega.event;

import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModMenus;
import com.swacky.ohmega.common.inv.AccessoryInventoryButton;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.network.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Ohmega.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SuppressWarnings("all")
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.ACCESSORY_INVENTORY.get(), (MenuScreens.ScreenConstructor<AccessoryInventoryMenu, AccessoryInventoryScreen>) AccessoryInventoryScreen::new);
            ModNetworking.register();
        });
    }

    @SubscribeEvent
    public static void addToScreens(ScreenEvent.InitScreenEvent.Post event) {
        if(event.getScreen() instanceof EffectRenderingInventoryScreen<?> screen) {
            Minecraft mc = event.getScreen().getMinecraft();
            if(mc.player != null) {
                if (!mc.player.isCreative()) {
                    event.addListener(new AccessoryInventoryButton(screen, 0, 0, 132, 22, 19));
                }
            }
        }
    }
}
